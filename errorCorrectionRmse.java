import java.util.ArrayList;
import java.util.Scanner;

/**
 * ErrorCorrectionLearning
 */
public class errorCorrectionRmse {
  public static double[] erRMSE(double[][] inputValues, double[] desiredOutputs, boolean randomWeightsOrNo, double tolerance) {
    //this is holding the weights in another array cause it'll
    //be easier for me to see and work with visually
    double[] weights = new double[(inputValues.length + 1)];
    

    double num;
    if (randomWeightsOrNo) {
      for (int i = 0; i < weights.length; i++) {
        //makes random decimal number up to the 1000ths between -0.5 and 0.5
        num = (int)((Math.random() - 0.5) * 1000);
        weights[i] = (num / 1000);
        // weights[i] = 1;
      }
    }
    else {
      Scanner input = new Scanner(System.in);
      for (int i = 0; i < weights.length; i++) {
        System.out.println("Enter weight number " + i + ": ");
        weights[i] = input.nextDouble();
      }
      // input.close();
      System.out.println();
      input.close();
    }



    System.out.println("Starting weights: ");
    printDoubleArr(weights);
    System.out.println();
    //This will keep track of whether or not the weights had to be
    //changed at all
    //flag statement
    boolean learning = true;

    //This will keep track of the amount of iterations that have taken
    //place and if it becomes more than 1000 it'll end the learning
    int amountOfIterations = 0;

    //This is going to hold the z values that will be inputted into the 
    //activation function
    double[] zValues;

    //This is going to hold the error
    double delta;

    //This will hold the values of eaceh RMSE that had to be found
    ArrayList<Double> rmseValues = new ArrayList<Double>();

    //As this loop progressses, it should actively update the weights
    //which should also be actively updating the zValues as a result of
    //intaking the updated weighting values


    double learningRate = (1.0 / weights.length);
    double mse;
    while(learning) {
      learning = false;
      amountOfIterations++;
      mse = 0.0;

      for (int i = 0; i < inputValues[0].length; i++) {
        zValues = zFunc(weights, inputValues);
        if (Math.tanh(zValues[i]) != desiredOutputs[i]) {
          delta = desiredOutputs[i] - Math.tanh(zValues[i]);
          mse = mse + (delta * delta);
        }
        else {
          mse += 0;
        }
      }


      rmseValues.add(rMSE(mse, inputValues));
      //rmseValues.get(amountOfIterations - 1)
      // System.out.println("mse this iteration: " + (mse / inputValues.length));
      // System.out.println("rmse this iteration: " + rMSE(mse, inputValues));
      //System.out.println("Iteration: " + amountOfIterations + "\n");
      if (rMSE(mse, inputValues) <= tolerance) {
        System.out.println("Iterations taken: " + amountOfIterations);

        System.out.println("\nThe RMSE values: ");
        printObjDoubleArr(rmseValues);

        System.out.println("\nInterpreted weights: ");
        printDoubleArr(interpretedValues(weights));

        System.out.println("\nThe final weights:");
        return weights;
      }


      for (int i = 0; i < inputValues[0].length; i++) {
        zValues = zFunc(weights, inputValues);
        if (Math.tanh(zValues[i]) != desiredOutputs[i]) {
          //if the weights need to be changed it means the learning is not 
          //done yet
          delta = desiredOutputs[i] - Math.tanh(zValues[i]);
          // System.out.println("\n\nActivationFunc of " + zValues[i] + ": " + activationFunc(zValues[i]) + "\n\n");

          weights[0] = weights[0] + (delta / (inputValues[0].length));
          for (int w = 1; w < weights.length; w++) {
              if (inputValues[w - 1][i] == 0) {
                weights[w] = weights[w];
              }
              else {
                weights[w] += (learningRate) * (delta) * ( (inputValues[w - 1][i]/1));
              }
              
          }
        }
      }
      learning = true;
    }
    //The new array of weights should now be created
    return weights;
}

public static double[] interpretedValues(double[] arr) {
  int k = arr.length;
  double[] discreteVersion = new double[arr.length];
  for (int i = 0; i < discreteVersion.length; i++) {
    discreteVersion[i] = (int)(arr[i] * 100) / 100.0;
  }
  for (int i = 0; i < arr.length; i++) {
    for (int j = 0; j < arr.length; j++) {
      if (discreteVersion[i] < (-1.0 + ((j+1) * (2.0 / k))) && discreteVersion[i] > (-1.0 + (j * (2.0 / k)))) {
        discreteVersion[i] = j;
        break;
      }
    }
  }
  return discreteVersion;
}

public static double[][] discreteInputs(double[][] arr) {
  int k = arr.length + 1;
  double[][] discreteVersion = arr;
  for (int i = 0; i < discreteVersion.length; i++) {
    for (int j = 0; j < discreteVersion[i].length; j++) {
      discreteVersion[i][j] = (-1.0 + discreteVersion[i][j]*(2.0 / k)) + (1.0 / k);
    }
  }
  return discreteVersion;
}

public static double [] discreteOutputs(double[] arr, int length) {
  int k = length + 1;
  double [] discreteVersion = new double[arr.length];
  for (int i = 0; i < discreteVersion.length; i++){
    discreteVersion[i] = (-1.0 + discreteVersion[i]*(2.0 / k)) + (1.0 / k);
  }
  return discreteVersion;
}


public static double rMSE(double mseSum, double[][] samples) {
  double total = mseSum;
  //total / num of samples
  total /= samples.length;
  total = Math.sqrt(total);
  return total;
}


public static double[] giveWeightArray (int size) {
  double num;
  double[] arr = new double[size];
  for (int i = 0; i < size; i++) {
    //makes random decimal number up to the 1000ths between -0.5 and 0.5
    num = (int)((Math.random() - 0.5) * 1000);
    arr[i] = (num / 1000);
  }
  return arr;
}

public static double[] zFunc(double[] weights, double[][] inputs) {
    double[] z = new double[inputs[0].length];
    for (int i = 0; i < inputs[0].length; i++) {
      z[i] += weights[0];
      for (int j = 0; j < inputs.length; j++) {
          z[i] += (weights[j + 1] * inputs[j][i]);
      }
    }


    return z;
}

public static int activationFunc(double zValue) {
  if (zValue > 0) {
    return 1;
  }
  else {
    return -1;
  }
}

public static void printObjDoubleArr(ArrayList<Double> arr) {
  System.out.print("[");
    for (int i = 0; i < arr.size(); i++) {
        if (i != arr.size() - 1) {
            System.out.print(arr.get(i) + " , ");
        }
        else {
            System.out.print(arr.get(i));
        }
       
    }
    System.out.print("]\n" );
}
public static void printDoubleArr(double[] arr) {
    System.out.print("[");
    for (int i = 0; i < arr.length; i++) {
        if (i != arr.length - 1) {
            System.out.print(arr[i] + " , ");
        }
        else {
            System.out.print(arr[i]);
        }
       
    }
    System.out.print("]\n" );
}


  public static void main(String[] args) {
    double[] x1 = {0, 2, 1};
    double[] x2 = {2, 2, 1};
    double[] x3 = {1, 3, 3};
    double[][] arrayOfInputs = {x1, x2, x3};
    double[] desiredOutputs = {0, 1, 2};

    double[][] discreteInputsVersion = discreteInputs(arrayOfInputs);
    double[] discreteOutputsVersion = discreteOutputs(desiredOutputs, arrayOfInputs.length);

    
    System.out.println("Tolerance Threshold of 1/4");
    printDoubleArr(erRMSE(discreteInputsVersion, discreteOutputsVersion, true, (1.0/4.0)));
  }
}