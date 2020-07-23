package ai;

import ai.NNlib.Activation;
import ai.NNlib.Initializer;
import ai.NNlib.Layer.*;
import ai.NNlib.LossFunction;
import ai.NNlib.NN;
import ai.NNlib.Optimizer;

public class ChessNN {

    private static final NN fc = new NN("fc", 0, .0001f, LossFunction.QUADRATIC(.5), Optimizer.AMSGRAD,
            new Dense(768, 128, Activation.TANH, Initializer.XAVIER),
            new Dense(128, 64, Activation.TANH, Initializer.XAVIER),
            new Dense(64, 1, Activation.TANH, Initializer.XAVIER)
    );

    private static final NN cnn = new NN("cnn", 0, .01f, LossFunction.QUADRATIC(.5), Optimizer.AMSGRAD,
            new Conv(12, 12, 3, 3, 1, 0, 0, Activation.TANH),//12-8-8 conv(s=1) 12-12-3-3 = 12-6-6
            new Conv(32, 12, 3, 3, 1, 0, 0, Activation.TANH),//12-6-6 conv(s=1) 128-12-3-3 = 128-4-4
            new Flatten(32, 4, 4),
            new Dense(512, 1, Activation.SIGMOID, Initializer.XAVIER)
    );

    public static final NN nn = cnn;
    
    static{
        System.out.println(cnn.getParameterCount());
    }
}
