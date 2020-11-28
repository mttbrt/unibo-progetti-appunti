from common import *
import os
from math import ceil
from random import shuffle
import numpy as np
from datetime import datetime

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # suppress logging
from tensorflow.keras.models import load_model

from music21 import note, chord, instrument
from music21.duration import Duration
from music21.stream import Score, Stream

from train_chords_mlp import make_chords as make_chords_mlp


def get_chord_model(name=None, use_tcn=False):
    if use_tcn:
        chord_model_directory = chord_tcn_model_directory
    else:
        chord_model_directory = chord_mlp_model_directory

    if name == None:
        all_subdirs = [os.path.join(chord_model_directory, d) for d in os.listdir(chord_model_directory) if os.path.isdir(os.path.join(chord_model_directory, d))]
        if all_subdirs == []:
            raise RuntimeError('No suitable models could be found. Please train a TCN or perceptron chord model before attempting generation.')
        name = os.path.basename(os.path.normpath(max(all_subdirs, key=os.path.getmtime)))

    print(f'Loading {"TCN" if use_tcn else "perceptron"} chord model {name}...')
    return load_model(os.path.join(chord_model_directory, name))

def get_poly_model(name=None):
    if name == None:
        all_subdirs = [os.path.join(poly_model_directory, d) for d in os.listdir(poly_model_directory) if os.path.isdir(os.path.join(poly_model_directory, d))]
        if all_subdirs == []:
            raise RuntimeError('No suitable models could be found. Please train a polyphonic model before attempting generation.')
        name = os.path.basename(os.path.normpath(max(all_subdirs, key=os.path.getmtime)))

    print(f'Loading TCN polyphonic model {name}...')
    return load_model(os.path.join(poly_model_directory, name))

def expand_chords(chords):
    new_chords = []
    for chord in chords:
        for _ in range(8):
            new_chords.append(chord)
    return new_chords

def make_model_input(chord_sequence, poly_sequence):
    assert len(chord_sequence) > len(poly_sequence)
    sequence = []
    for i in range(len(poly_sequence)):
        features = pianoroll_to_multi_hot(poly_sequence[i])
        features.extend(index_to_one_hot(chord_sequence[i]))
        features.extend(index_to_one_hot(chord_sequence[i+1]))
        features.append(i%8)
        sequence.append(features)
    return sequence

def neighborhood(array, position, radius):
    far_left = position-radius
    far_right = position+radius
    if far_left < 0:
        return array[:2*radius + 1 + far_left]
    elif far_right >= len(array):
        return array[-(2*radius + 1 + len(array) - 1 - far_right):]
    else:
        return array[far_left:far_right+1]

def contains_one(array):
    for elem in array:
        if elem == 1:
            return True
    return False

def adjust_probabilities(pred, prior):
    assert len(pred) == len(prior)
    distribution = []
    for i, prob in enumerate(pred):
        new_prob = prob
        if contains_one(neighborhood(prior,i,4)):
            distribution.append(prob*2-prob**2)
        else:
            distribution.append(prob)
    return distribution

def sample_piano_roll(probabilities, use_heuristics=False):
        if not use_heuristics:
            return [np.random.choice([0, 1], 1, p=[1-note_p, note_p]) for note_p in probabilities]
        else:
            assert len(probabilities) == midi_range
            note_indices = range(midi_range)
            shuffle(note_indices) #prevents biases towards notes "on the left" of a dissonant interval
            notes_sampled = [0 for i in range(midi_range)]
            for i in note_indices:
                p = probabilities[i]
                play = np.random.choice([False, True], 1, p=[1-p, p])
                if play:
                    notes_sampled[i] = 1
                    for idi in immediate_dissonant_intervals:
                        try:
                            notes_sampled[i+idi] = 0.5 * notes_sampled[i+idi]
                        except IndexError:
                            continue

def make_music(chord_model, poly_model, chord_seed, poly_seed, beats, use_heuristics=False):
    # generate chords
    measures = ceil(beats/8) + 1 # +1 so that we do not run out of chords when using future chord features
    chord_sequence = make_chords_mlp(chord_model, chord_seed, measures, indices=True)
    chord_sequence = expand_chords(chord_sequence)

    print(f'Generated chords:')
    for i in range(0, len(chord_sequence), 8):
        print(f'| {" ".join([str(v) for v in chord_sequence[i:i+8]])}', end=' ')
    print('|', end='\n\n')

    # generate rolls
    poly_seed_len = len(poly_seed)
    poly_sequence = poly_seed
    for i in range(beats):
        model_input = np.array([make_model_input(chord_sequence, poly_sequence)])
        poly_prediction = poly_model.predict(model_input)[0][-1]
        poly_probabilities = poly_prediction
        if use_heuristics:
            poly_probabilities = adjust_probabilities(poly_probabilities, model_input[0,-1,:midi_range])
            #Add other adjustments here
        new_piano_roll = sample_piano_roll(poly_probabilities)
        poly_sequence.append(multi_hot_to_pianoroll(new_piano_roll))

    build_midi(chord_sequence, poly_sequence[poly_seed_len:])

def build_midi(harmony, melody):
    chords_dict = get_chord_dicts()[1]

    song = []
    for i, eighth in enumerate(melody):
        # eighth = multi_hot_to_pianoroll(piano_roll[:midi_range]) # now make_music returns pianorolls already
        # chord = one_hot_to_index(piano_roll[-chord_classes:]) # TODO add chord to midi
        # print(f'EIGHTH: {eighth}') # DEBUG

        song_notes = []
        for note_ in eighth:
            note_name = NOTES[note_%12]
            note_octave = start_octave + note_//12 # starting from C2
            song_notes.append(note_name + str(note_octave))

        song_chords = []
        full_chord = chords_dict[harmony[i]]
        if full_chord != '<unk>':
            for chord_ in full_chord:
                chord_name = NOTES[chord_%12]
                song_chords.append(chord_name + str(start_octave-1))

        song.append(("REST" if len(song_notes) == 0 else song_notes, "REST" if len(song_chords) == 0 else song_chords))

    notes_score = Score()
    notes_score.append(instrument.Piano())
    chords_score = Score()
    chords_score.append(instrument.KeyboardInstrument())
    bass_score = Score()
    bass_score.append(instrument.ElectricBass())

    current_note_length = 0
    current_chord_length = 0

    for i, _ in enumerate(song):

        current_note_length += 0.5
        current_chord_length += 0.5

        # print(f'NOTE: {song[i][0]}\t\t\t- CHORD: {song[i][1]}')

        if i < len(song)-1:
            # note
            if song[i][0] != song[i+1][0]:
                if song[i][0] == "REST":
                    notes_score.append(note.Rest(duration=Duration(current_note_length)))
                else:
                    notes_score.append(chord.Chord([note.Note(nameWithOctave=note_name) for note_name in song[i][0]], duration=Duration(current_note_length)))
                current_note_length = 0

            # chord
            if song[i][1] != song[i+1][1] or current_chord_length == 4:
                if song[i][1] == "REST":
                    chords_score.append(note.Rest(duration=Duration(current_chord_length)))

                    bass_score.append(note.Rest(duration=Duration(current_chord_length/4)))
                    bass_score.append(note.Rest(duration=Duration(current_chord_length/4)))
                    bass_score.append(note.Rest(duration=Duration(current_chord_length/2)))
                else:
                    chords_score.append(chord.Chord([note.Note(nameWithOctave=chord_name) for chord_name in song[i][1]], duration=Duration(current_chord_length)))

                    bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/4)))
                    bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/4)))
                    bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/2)))
                current_chord_length = 0
        else:
            # note
            if song[i][0] == "REST":
                notes_score.append(note.Rest(duration=Duration(current_note_length)))
            else:
                notes_score.append(chord.Chord([note.Note(nameWithOctave=note_name) for note_name in song[i][0]], duration=Duration(current_note_length)))

            # chord
            if song[i][1] == "REST":
                chords_score.append(note.Rest(duration=Duration(current_chord_length)))

                bass_score.append(note.Rest(duration=Duration(current_chord_length/4)))
                bass_score.append(note.Rest(duration=Duration(current_chord_length/4)))
                bass_score.append(note.Rest(duration=Duration(current_chord_length/2)))
            else:
                chords_score.append(chord.Chord([note.Note(nameWithOctave=chord_name) for chord_name in song[i][1]], duration=Duration(current_chord_length)))

                bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/4)))
                bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/4)))
                bass_score.append(chord.Chord([note.Note(nameWithOctave=chord_name[:-1]+str(int(chord_name[-1])+1)) for chord_name in song[i][1]], duration=Duration(current_chord_length/2)))

    song_stream = Stream()
    song_stream.insert(0, notes_score)
    song_stream.insert(0, chords_score)
    song_stream.insert(0, bass_score)

    if not os.path.exists('melodies'):
        os.makedirs('melodies')
    dt = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")
    song_stream.write('midi', fp=f'melodies/generated_{dt}.mid')

if __name__ == '__main__':
    #get seed
    print('Getting seeds...')
    use_heuristics = False
    seed_song = 392 # choose a song index
    seed_measure = 10 # choose starting measure
    seed_measures_num = 16 # number of measures

    chord_indices = get_chord_indices(debug_print=seed_song)
    dataset_size = len(chord_indices)
    piano_rolls = get_piano_rolls()
    assert len(piano_rolls) == dataset_size

    chord_seed, poly_seed = chord_indices[seed_song][seed_measure:seed_measure+seed_measures_num], piano_rolls[seed_song][seed_measure*8:(seed_measure+seed_measures_num)*8]

    #get models
    print('Getting models...')
    chord_model = get_chord_model(name=None) #insert specific model identifier if needed
    poly_model = get_poly_model(name=None) #insert specific model identifier if needed

    print('Making music...')
    make_music(chord_model, poly_model, chord_seed, poly_seed, 200, use_heuristics=use_heuristics)

    print('Done.')
