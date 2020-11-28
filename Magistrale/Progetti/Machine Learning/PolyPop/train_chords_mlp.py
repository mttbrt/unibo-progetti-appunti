import logging, os
from common import *

import numpy as np
from tensorflow.keras import Sequential
from tensorflow.keras.layers import Embedding, Dense, Flatten, Input, Dropout
from tensorflow.keras.models import load_model


def get_x_y(chord_indices, window_size, embed=False):

    x = []
    y = []
    for chord_index in chord_indices:
        for i in range(len(chord_index) - window_size - 1):
            if embed:
                x.append(chord_index[i:i+window_size])
            else:
                x.append(list(map(index_to_one_hot, chord_index[i:i+window_size])))
            y.append(index_to_one_hot(chord_index[i + window_size]))
    return np.array(x), np.array(y)


def load_chord_model(name=None):
    if name == None:
        all_subdirs = [os.path.join(chord_mlp_model_directory, d) for d in os.listdir(chord_mlp_model_directory) if os.path.isdir(os.path.join(chord_mlp_model_directory, d))]
        if all_subdirs == []:
            raise RuntimeError('No suitable models could be found. Please train a perceptron chord model before attempting generation.')
        name = os.path.basename(os.path.normpath(max(all_subdirs, key=os.path.getmtime)))

    print(f'Loading perceptron chord model {name}...')
    return load_model(os.path.join(chord_mlp_model_directory, name))

def make_model(window_size, embed=False):

    model = Sequential()
    if embed:
        model.add(Embedding(input_dim=chord_classes, output_dim=10, input_length=window_size))
    else:
        model.add(Input(shape=(window_size, chord_classes)))
    model.add(Flatten())
    model.add(Dense(units=50, activation='relu'))
    model.add(Dropout(rate=0.3))
    model.add(Dense(units=50, activation='softmax'))
    model.compile(optimizer='adam',loss='categorical_crossentropy',metrics=['accuracy'])

    return model
	
def make_chords(model, seed, measures, indices=False):
    chord_to_index_dict, index_to_chord_dict = get_chord_dicts(data_directory='train_data')
    model_input_size = model.layers[0].input_shape[1]
    if indices:
        primer = seed
    else:
        primer = []
        for chord in seed:
            if chord not in chord_to_index_dict:
                raise ValueError(f'Chord {chord} is not a chord in the original data.')
            primer.append(chord_to_index_dict[chord])

    sequence = primer
    for i in range(measures):
        model_input = np.asarray([sequence[-model_input_size:]])
        new_chord = model.predict_classes(model_input)[0]
        sequence.append(new_chord)

    return list(sequence)



if __name__ == '__main__':

    epochs = 20
    batch_size=32
    window_size = 10
    embed = True

    chord_indices = get_chord_indices(data_directory='train_data')
    x,y = get_x_y(chord_indices, window_size, embed=embed)
    print(x.shape, y.shape)

    model = make_model(window_size, embed=embed)
    #model = load_chord_model()
    model.summary()

    hist = model.fit(x, y, epochs=epochs, batch_size=batch_size, validation_split=0.2, shuffle=True)
    plot_history(hist, accuracy=True, validation=True)

    dump_model(model, './models/chords/mlp/', epochs, batch_size, window_size, desc='-embed' if embed else '-onehot')
