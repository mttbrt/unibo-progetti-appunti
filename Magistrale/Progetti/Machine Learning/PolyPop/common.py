import os
from glob import glob
import pickle
import matplotlib.pyplot as plt
from datetime import datetime
from tensorflow.keras.models import load_model

chord_mlp_model_directory = "./models/chords/mlp"
chord_tcn_model_directory = "./models/chords/tcn"
poly_model_directory = "./models/poly"

chord_classes = 50
no_chord = 0

top_midi_note = 84 #C6
bottom_midi_note = 36 #C2
midi_range = (top_midi_note - bottom_midi_note) + 1 #49
start_octave = 3 # C3
immediate_dissonant_intervals = [-2,-1,1,2] #major and minor seconds

NOTES = ['C', 'C#', 'D', 'D#', 'E', 'F', 'F#', 'G', 'G#', 'A', 'A#', 'B']

#IN

def get_chord_indices(debug_print=None, data_directory='data'):
    chord_indices = []
    chord_files = glob(os.path.join(data_directory,"shifted","chord_index","*.pickle"))
    for i, chord_file in enumerate(chord_files):
        if i == debug_print:
            print(f'Using {i}-th file: "{chord_file}" as seed')
        with open(chord_file, "rb") as cf:
            chord_index = pickle.load(cf)
        chord_indices.append(chord_index)
    return chord_indices

def get_chord_dicts(data_directory='data'):
    with open(os.path.join(data_directory,"chord_dict_shifted.pickle"),"rb") as cdf:
        chord_to_index_dict = pickle.load(cdf)
    with open(os.path.join(data_directory,"index_dict_shifted.pickle"),"rb") as idf:
        index_to_chord_dict = pickle.load(idf)
    return chord_to_index_dict, index_to_chord_dict

def get_piano_rolls(data_directory='data'):
    piano_rolls = []
    pianoroll_files = glob(os.path.join(data_directory,"shifted","indroll","*.pickle"))
    for pianoroll_file in pianoroll_files:
        with open(pianoroll_file, "rb") as pf:
            pianoroll = pickle.load(pf)
        piano_rolls.append(pianoroll)
    return piano_rolls

def index_to_one_hot(chord_index):
    if chord_index >= chord_classes:
        raise ValueError(f'Index {chord_index} is not a valid chord index')
    return [1 if i == chord_index else 0 for i in range(chord_classes)]

def one_hot_to_index(vector):
    if len(vector) != chord_classes:
        raise ValueError(f'One-hot encoded vector {vector} has size {len(vector)}, expected {chord_classes}')
    for i in range(len(vector)):
        if vector[i] == 1:
            return i
    raise ValueError(f'One-hot encoded vector {vector} is not a valid one-hot encoded vector')

def pianoroll_to_multi_hot(pr_slice):
    return [1 if i in pr_slice else 0 for i in range(bottom_midi_note, top_midi_note+1)]

def multi_hot_to_pianoroll(vector):
    if len(vector) != midi_range:
        raise ValueError(f'Multi-hot encoded vector {vector} has size {len(vector)}, expected {midi_range}')
    notes = []
    for i in range(len(vector)):
        if vector[i] == 1:
            notes.append(i)
    return tuple(notes)


#OUT

def plot_history(history, accuracy=False, validation=False):
    if accuracy:
        # summarize history for accuracy
        plt.plot(history.history['accuracy'])
        if validation:
            plt.plot(history.history['val_accuracy'])
        plt.title('model accuracy')
        plt.ylabel('accuracy')
        plt.xlabel('epoch')
        legend = ['train', 'validation'] if validation else ['train']
        plt.legend(legend, loc='upper left')
        plt.show()
    # summarize history for loss
    plt.plot(history.history['loss'])
    if validation:
        plt.plot(history.history['val_loss'])
    plt.title('model loss')
    plt.ylabel('loss')
    plt.xlabel('epoch')
    legend = ['train', 'validation'] if validation else ['train']
    plt.legend(legend, loc='upper left')
    plt.show()

def dump_model(model, directory, epochs, batch_size, window_size=None, desc=''):
    save_folder = datetime.now().strftime("%Y-%m-%d_%H-%M-%S") + f'-e{epochs}-b{batch_size}'
    if window_size != None:
        save_folder += f'-w{window_size}'
    save_folder += desc
    model.save(os.path.join(directory, save_folder))

def load_poly_model(name=None):
    if name == None:
        all_subdirs = [os.path.join(poly_model_directory, d) for d in os.listdir(poly_model_directory) if os.path.isdir(os.path.join(poly_model_directory, d))]
        if all_subdirs == []:
            raise RuntimeError('No suitable models could be found. Please train a polyphonic model before attempting generation.')
        name = os.path.join(poly_model_directory, os.path.basename(os.path.normpath(max(all_subdirs, key=os.path.getmtime))))

    print(f'Loading TCN polyphonic model: {name}...')
    return load_model(name)
