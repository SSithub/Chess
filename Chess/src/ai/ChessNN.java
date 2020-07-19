package ai;

import ai.NNLib.ActivationFunction;
import ai.NNLib.Initializer;
import ai.NNLib.Layer;
import ai.NNLib.LossFunction;
import ai.NNLib.NN;
import ai.NNLib.Optimizer;

public class ChessNN {
    public static final NN nn = new NN("chessAI", 7777, .0001, LossFunction.QUADRATIC(.5), Optimizer.AMSGRAD,
            new Layer.Dense(768, 128, ActivationFunction.TANH, Initializer.XAVIER),
            new Layer.Dense(128, 64, ActivationFunction.TANH, Initializer.XAVIER),
            new Layer.Dense(64, 1, ActivationFunction.TANH, Initializer.XAVIER));
}
