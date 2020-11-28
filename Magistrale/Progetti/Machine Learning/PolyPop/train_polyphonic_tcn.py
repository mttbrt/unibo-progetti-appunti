from common import *
from tcn_evaluation import Evaluation

import numpy as np
from tensorflow.keras import Input, Model
from tensorflow.keras.layers import Activation, Dense
from tcn import TCN

def get_x_y_gen(chord_indices, piano_rolls):
    while True:
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
            yield sequence[:,:-1,:], sequence[:,1:,:midi_range]

def make_model():

   i = Input(shape=(None, midi_range+2*chord_classes+1))

   o = TCN(nb_filters=150,
           kernel_size=8,
           nb_stacks=1,
           dilations=[2 ** i for i in range(6)],
           use_layer_norm=True,
           dropout_rate=0.3,
           return_sequences=True,
       )(i)
   o = Dense(midi_range)(o)
   o = Activation('sigmoid')(o)

   model = Model(i, o)
   model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

   return model


if __name__ == '__main__':

    epochs = 50
    batch_size = 1

    train_chords = get_chord_indices(data_directory='train_data')
    train_pianorolls = get_piano_rolls(data_directory='train_data')
    train_gen = get_x_y_gen(train_chords, train_pianorolls)

    model = make_model()
    # model = load_poly_model()
    # model = tcn.build_TCN()
    model.summary()

    evaluation = Evaluation(epochs=epochs, interval=5)
    hist = model.fit(train_gen, steps_per_epoch=len(train_chords)//batch_size, epochs=epochs, callbacks=[evaluation])
    # dump_model(model, './models/poly/', epochs, batch_size)
    # plot_history(hist, accuracy=True)

    print('Done')
