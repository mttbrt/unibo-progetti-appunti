import tensorflow as tf
from tensorflow.keras.callbacks import Callback
import numpy as np
import statistics, logging, time
from common import *
from eval_core_func import *
from progress.bar import Bar
import sys, time

from eval_core_func import *

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

    def import_model(self, name=None, model=None):
        if model != None:
            self.model = model
        else:
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

                F, H, H_tr, H_ss = compute_metrics(target, output, prediction)
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

if __name__ == '__main__':
    evaluation = Evaluation(interval=1)
    evaluation.import_model(sys.argv[1] if len(sys.argv) > 1 else None)
    evaluation.evaluate()
