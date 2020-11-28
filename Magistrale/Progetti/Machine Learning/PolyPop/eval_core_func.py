import math, sys

# ---------- EVALUATION METRICS ----------

# target: binary matrix representing actual notes played
# output: matrix representing the sigmoid output of the network
# prediction: output matrix where is applied the probability for each note of being played or not

def compute_metrics(target, output, prediction):
    assert len(target) == len(output) == len(prediction)
    tot_timesteps = len(target)
    midi_range = len(target[0])
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


def F_score(target, prediction):
    assert len(target) == len(prediction)
    tot_timesteps = len(target)
    midi_range = len(target[0])

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

def cross_entropy(trg, out):
    midi_range = len(target)
    return -sum([trg[p] * math.log(out[p] if out[p] > 0 else sys.float_info[3]) + (1 - trg[p]) * math.log((1 - out[p]) if (1 - out[p]) > 0 else sys.float_info[3]) for p in range(midi_range)])

def sequence_cross_entropy(target, output):
    assert len(target) == len(output)
    tot_timesteps = len(target)

    H = 0
    for t in range(tot_timesteps):
        H += cross_entropy(target[t], output[t])
    H /= tot_timesteps

    return H

def transition_cross_entropy(target, output):
    assert len(target) == len(output)
    tot_timesteps = len(target)
    midi_range = len(target[0])

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
        H_tr += cross_entropy(target[t], output[t]) / d[i]
    H_tr /= len(Tr)

    return H_tr

def steady_state_cross_entropy(target, output):
    assert len(target) == len(output)
    tot_timesteps = len(target)
    midi_range = len(target[0])

    Ts = []
    for t in range(1, tot_timesteps):
        # number of different notes between two eighths
        diff_notes = sum([1 if target[t][p] != target[t-1][p] else 0 for p in range(midi_range)])

        if diff_notes == 0:
            Ts.append(t)

    H_ss = 0
    for i, t in enumerate(Ts):
        H_ss += cross_entropy(target[t], output[t])
    H_ss /= len(Ts)

    return H_ss
