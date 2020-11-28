import tensorflow as tf
from tensorflow.keras.callbacks import Callback
from tensorflow.keras.models import load_model
import numpy as np
import math, statistics, logging, time
from progress.bar import Bar
import sys, time, os

import data_class
import chord_model
from settings import *

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from eval_core_func import *
from common import *

tf.get_logger().setLevel(logging.ERROR)

class Evaluation(Callback):
    def __init__(self, epochs=1, interval=5, chord_embed_model=None):
        super().__init__()
        self.chord_embed_model = chord_embed_model

        test_chords = get_chord_indices(data_directory='../test_data')
        test_pianorolls = get_piano_rolls(data_directory='../test_data')
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
        if epoch == self.epochs:
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
            self.model = load_model(name)

    def get_x_y_collection(self, chord_indices, piano_rolls):
        X, Y = [], []

        for i, song in enumerate(piano_rolls):
            x, y = self.make_feature_vector(song, chord_indices[i], 'embed')
            X.append(x)
            Y.append(y)

        return X, Y

    def make_feature_vector(self, song, chords, chord_embed_method):
        if  next_chord_feature:
            X = np.array(data_class.make_one_hot_note_vector(song[:(((len(chords)-1)*fs*2)-1)], num_notes))
        else:
            X = np.array(data_class.make_one_hot_note_vector(song[:((len(chords)*fs*2)-1)], num_notes))
        X = X[:,low_crop:high_crop]

        if chord_embed_method == 'embed':
            X_chords = list(self.chord_embed_model.embed_chords_song(chords))
        elif chord_embed_method == 'onehot':
            X_chords = data_class.make_one_hot_vector(chords, num_chords)
        elif chord_embed_method == 'int':
            X_chords = [[x] for x in chords]
        X_chords_new = []
        Y = X[1:]

        for j, _ in enumerate(X):
            ind = int(((j+1)/(fs*2)))

            if next_chord_feature:
                ind2 = int(((j+1)/(fs*2)))+1
                X_chords_new.append(list(X_chords[ind])+list(X_chords[ind2]))
            else:
                X_chords_new.append(X_chords[ind])

        X_chords_new = np.array(X_chords_new)
        X = np.append(X, X_chords_new, axis=1)

        if counter_feature:
            counter = [[0,0,0],[0,0,1],[0,1,0],[0,1,1],[1,0,0],[1,0,1],[1,1,0],[1,1,1]]
            if next_chord_feature:
                counter = np.array(counter*(len(X_chords)-1))[:-1]
            else:
                counter = np.array(counter*len(X_chords))[:-1]
            X = np.append(X, counter, axis=1)

        X = X[:-1]
        X = np.reshape(X, (X.shape[0], 1, X.shape[1]))

        return X, Y

    def evaluate(self, epoch=0):
        if epoch % self.interval == 0:
            f_scores = []
            sequence_cross_entropies = []
            transition_cross_entropies = []
            steady_state_cross_entropies = []

            print(f'\nModel validation:')
            bar = Bar('Validating', fill='\u2588', suffix='%(percent).1f%%', max=len(self.x_test))
            for i, song in enumerate(self.x_test):
                target = self.y_test[i]
                output = self.model.predict(song, batch_size=1)
                self.model.reset_states()
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
    if len(sys.argv) > 1:
        if len(sys.argv) > 2:
            evaluation = Evaluation(interval=1, chord_embed_model=chord_model.Embed_Chord_Model(sys.argv[1]))
            evaluation.import_model(name=sys.argv[2])
            evaluation.evaluate()
        else:
            print('Please specify the polyphonic model path.')
    else:
        print('Please specify the chord model path.')
