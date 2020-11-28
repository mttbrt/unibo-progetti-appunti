# NOTE: UNUSED
# This is a "homemade" implementation of a temporal convolutional network.
# We used this to test whether the Keras-TCN implementation was behaving the way we intended (which it is).
# We do NOT use this implementation in any of the models hereby presented

from tensorflow.keras.layers import Input, Conv1D, BatchNormalization, Activation, SpatialDropout1D, Dense
from tensorflow.keras.initializers import RandomNormal
from tensorflow.keras import Model
from copy import copy

def build_TCN(n_features=150, kernel_size=6, dilations=[1, 2, 4, 8, 16, 32], enable_res_conn=False, res_dropout=0.3):
    input = Input(shape=(None, 150))
    output = input

    # Stack of Causal Dilated Convolutions
    for i, dilation_rate in enumerate(dilations):
        prev_output = copy(output)

        for _ in range(2):
            output = Conv1D(n_features, kernel_size, padding='causal', input_shape=input.shape, dilation_rate=dilation_rate, kernel_initializer=RandomNormal(stddev=0.01))(output)
            output = BatchNormalization(axis=-1)(output)
            output = Activation('relu')(output)
            output = SpatialDropout1D(res_dropout)(output)

        # Skip Connections
        output = Activation('relu')(prev_output + output)

    output = Dense(49)(output)
    output = Activation('sigmoid')(output)

    model = Model(inputs=input, outputs=output)
    model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])

    return model
