# PolyPop

Authors: Matteo Berti, Andrea Colledan

This repository hosts the source code for PolyPop, a polyphonic pop music generation model which makes use of Temporal Convolutional Networks (TCNs).

PolyPop's architecture is inspired by JamBot, a polyphonic music generation model based on LSTMs. NOTE: the "JamBot" directory contains a virtually unchanged copy of JamBot's source code, available at: https://github.com/brunnergino/JamBot.

## Setup

Because our model and JamBot require different dependencies, we strongly suggest the use of separate virtual environments to run PolyPop and JamBot. To install dependencies for our model:

```
$ virtualenv penv
$ ./penv/scripts/activate
$ pip install -r requirements.txt
```

To install dependencies for JamBot:

```
$ cd JamBot
$ virtualenv jenv
$ ./jenv/scripts/activate
$ pip install -r requirements.txt
```

## Training PolyPop

To run PolyPop you first need to train its chord and polyphonic models. To train a chord model, run the following command from the root directory:

```
$ python train_chords_mlp.py
```

The default training parameters (e.g. batch size, window size, etc.) are those described in the project report. Training parameters can be set directly from the source code, in `train_chords_mlp.py`'s main. Once a chord model is trained, a polyphonic model can be trained by running the following command:

```
$ python train_polyphonic_tcn.py
```

Again, training parameters can be set in `train_polyphonic_tcn.py`'s main. Note that the evaluation process is run by default every 5 epochs.

## Generating Music with PolyPop

To generate music with PolyPop, it is sufficient to run the following command:

```
$ python generate.py
```

By default, the generation process finds and uses the most recently trained chord and poylphonic models. To specify a different chord or polyphonic model, it is sufficient to replace `None` with the desired model identifier in the `get_chord_model(name=None)` and `get_poly_model(name=None)` function calls, respectively.
