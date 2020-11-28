import tensorflow as tf
from tensorflow.keras.callbacks import Callback
import numpy as np
import math, statistics, logging, time
from common import *
from progress.bar import Bar
import sys, time

tf.get_logger().setLevel(logging.ERROR)

class Evaluation(Callback):
    def __init__(self, epochs=1, interval=5):
        super().__init__()

        test_chords = get_chord_indices(data_directory='test_data')
        test_pianorolls = get_piano_rolls(data_directory='test_data')
        x_test, y_test = self.get_x_y_collection(test_chords, test_pianorolls)
        assert len(x_test) == len(y_test)

        self.interval = interval
        self.epochs = epochs
        self.x_test, self.y_test = x_test, y_test

        self.f_score = []
        self.sequence_cross_entropy = []
        self.transition_cross_entropy = []
        self.steady_state_cross_entropy = []

    def on_epoch_end(self, epoch, logs={}):
        self.evaluate(epoch)
        if epoch + 1 == self.epochs:
            if not os.path.exists('validation'):
                os.makedirs('validation')

            with open(os.path.join('validation','output_' + str(int(round(time.time() * 1000))) + '.txt'), 'a+') as output_file:
                output_file.write(f'f_score:{self.f_score}\n')
                output_file.write(f'sequence_cross_entropy:{self.sequence_cross_entropy}\n')
                output_file.write(f'transition_cross_entropy:{self.transition_cross_entropy}\n')
                output_file.write(f'steady_state_cross_entropy,{self.steady_state_cross_entropy}\n')

    def import_model(self, name=None):
        self.model = load_poly_model(name)

    def get_x_y_collection(self, chord_indices, piano_rolls):
        X, Y = [], []

        for i, song in enumerate(chord_indices):
            sequence = []
            for j, chord in enumerate(song[:-1]):
                for z in range(j*8, (j+1)*8):
                    datum = pianoroll_to_multi_hot(piano_rolls[i][z]) # get notes piano roll
                    datum.extend(index_to_one_hot(chord)) # add current chord
                    datum.extend(index_to_one_hot(song[j+1])) # add next chord
                    datum.append(z-(j*8)) # add eighth counter
                    sequence.append(datum)
            sequence = np.array([sequence])

            X.append(sequence[:,:-1,:])
            Y.append(sequence[:,1:,:-2*chord_classes-1])

        return X, Y

    def evaluate(self, epoch=-1):
        if (epoch + 1) % self.interval == 0:
            f_scores = []
            sequence_cross_entropies = []
            transition_cross_entropies = []
            steady_state_cross_entropies = []

            print(f'\nModel validation:')
            bar = Bar('Validating', fill='\u2588', suffix='%(percent).1f%%', max=len(self.x_test))
            for i, song in enumerate(self.x_test):
                target = self.y_test[i][0]
                output = self.model.predict(song)[0]
                prediction = [[np.random.choice([0, 1], 1, p=[1-note_p, note_p])[0] for note_p in eigth] for eigth in output]

                F, H, H_tr, H_ss = self.compute_metrics(target, output, prediction)
                f_scores.append(F)
                sequence_cross_entropies.append(H)
                transition_cross_entropies.append(H_tr)
                steady_state_cross_entropies.append(H_ss)

                # f_scores.append(self.F_score(target, prediction))
                # sequence_cross_entropies.append(self.sequence_cross_entropy(target, output))
                # transition_cross_entropies.append(self.transition_cross_entropy(target, output))
                # steady_state_cross_entropies.append(self.steady_state_cross_entropy(target, output))

                bar.next()

            self.f_score.append(round(statistics.mean(f_scores), 2))
            self.sequence_cross_entropy.append(round(statistics.mean(sequence_cross_entropies), 2))
            self.transition_cross_entropy.append(round(statistics.mean(transition_cross_entropies), 2))
            self.steady_state_cross_entropy.append(round(statistics.mean(steady_state_cross_entropies), 2))

            print(f'\nF-score:\t\t\t{self.f_score[-1]}')
            print(f'Cross entropy:\t\t\t{self.sequence_cross_entropy[-1]}')
            print(f'Transition cross entropy:\t{self.transition_cross_entropy[-1]}')
            print(f'Steady state cross entropy:\t{self.steady_state_cross_entropy[-1]}')



    # ---------- EVALUATION METRICS ----------

    # target: binary matrix representing actual notes played
    # output: matrix representing the sigmoid output of the network
    # prediction: output matrix where is applied the probability for each note of being played or not

    def compute_metrics(self, target, output, prediction):
        assert len(target) == len(output) == len(prediction)
        tot_timesteps = len(target)
        tr_timesteps = 0 # eighths where notes played are different from the ones in the previous eighth
        ss_timesteps = 0 # eighths where notes played are equal to the ones in the previous eighth

        # F-score variables
        TP = 0 # true positives
        FP = 0 # false positives
        FN = 0 # false negatives

        # Cross entropy variable
        H = 0

        # Transition cross entropy variables
        H_tr = 0

        # Steady state cross entropy variables
        H_ss = 0

        for t in range(tot_timesteps):
            H_sum = 0
            diff_notes = 0

            for p in range(midi_range):
                # f-score
                if target[t][p] == 1 and prediction[t][p] == 1:
                    TP += 1
                elif target[t][p] == 1 and prediction[t][p] == 0:
                    FN += 1
                elif target[t][p] == 0 and prediction[t][p] == 1:
                    FP += 1

                # cross entropy
                H_sum += target[t][p] * math.log(output[t][p] if output[t][p] > 0 else sys.float_info[3]) + (1 - target[t][p]) * math.log((1 - output[t][p]) if (1 - output[t][p]) > 0 else sys.float_info[3])

                # number of equal notes between two eighths
                if target[t][p] != target[t-1][p]:
                    diff_notes += 1

            H -= H_sum
            if diff_notes > 0:
                H_tr -= H_sum / diff_notes
                tr_timesteps += 1
            else:
                H_ss -= H_sum
                ss_timesteps += 1

        # f-score
        precision = TP / (TP + FP)
        recall = TP / (TP + FN)
        F = (2 * precision * recall) / (precision + recall)

        # cross entropy
        H /= tot_timesteps

        # transition cross entropy
        H_tr /= tr_timesteps

        # steady state cross entropy
        H_ss /= ss_timesteps

        return F, H, H_tr, H_ss


    def F_score(self, target, prediction):
        assert len(target) == len(prediction)
        tot_timesteps = len(target)

        TP = 0 # true positives
        FP = 0 # false positives
        FN = 0 # false negatives

        for t in range(tot_timesteps):
            for p in range(midi_range):
                if target[t][p] == 1 and prediction[t][p] == 1:
                    TP += 1
                elif target[t][p] == 1 and prediction[t][p] == 0:
                    FN += 1
                elif target[t][p] == 0 and prediction[t][p] == 1:
                    FP += 1

        precision = TP / (TP + FP)
        recall = TP / (TP + FN)

        F = (2 * precision * recall) / (precision + recall)

        return F

    def cross_entropy(self, trg, out):
        return -sum([trg[p] * math.log(out[p]) + (1 - trg[p]) * math.log(1 - out[p]) for p in range(midi_range)])

    def sequence_cross_entropy(self, target, output):
        assert len(target) == len(output)
        tot_timesteps = len(target)

        H = 0
        for t in range(tot_timesteps):
            H += self.cross_entropy(target[t], output[t])
        H /= tot_timesteps

        return H

    def transition_cross_entropy(self, target, output):
        assert len(target) == len(output)
        tot_timesteps = len(target)

        Tr = []
        d = [] # number of bins that differ between two eighths (if they are not identical)
        for t in range(1, tot_timesteps):
            # number of different notes between two eighths
            diff_notes = sum([1 if target[t][p] != target[t-1][p] else 0 for p in range(midi_range)])

            if diff_notes > 0:
                Tr.append(t)
                d.append(diff_notes)

        H_tr = 0
        for i, t in enumerate(Tr):
            H_tr += self.cross_entropy(target[t], output[t]) / d[i]
        H_tr /= len(Tr)

        return H_tr

    def steady_state_cross_entropy(self, target, output):
        assert len(target) == len(output)
        tot_timesteps = len(target)

        Ts = []
        for t in range(1, tot_timesteps):
            # number of different notes between two eighths
            diff_notes = sum([1 if target[t][p] != target[t-1][p] else 0 for p in range(midi_range)])

            if diff_notes == 0:
                Ts.append(t)

        H_ss = 0
        for i, t in enumerate(Ts):
            H_ss += self.cross_entropy(target[t], output[t])
        H_ss /= len(Ts)

        return H_ss


if __name__ == '__main__':
    evaluation = Evaluation(interval=1)
    evaluation.import_model(sys.argv[1] if len(sys.argv) > 1 else None)
    evaluation.evaluate()
