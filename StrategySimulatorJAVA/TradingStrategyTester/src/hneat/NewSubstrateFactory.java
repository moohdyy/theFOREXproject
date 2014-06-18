package hneat;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateNode;


	public class NewSubstrateFactory {

		/**
		 * Create a sandwich substrate. A sandwich has an input layer connected
		 * directly to an output layer, both are square.
		 * 
		 * @param inputEdgeSize The input edge size.
		 * @param outputEdgeSize The output edge size.
		 * @return The substrate.
		 */
		public static Substrate factorHLSubstrate(int inputEdgeSize,
				int outputEdgeSize) {
			Substrate result = new Substrate(3);

			double inputTick = 2.0 / inputEdgeSize;
			double outputTick = 2.0 / inputEdgeSize;
			double inputOrig = -1.0 + (inputTick / 2.0);
			double outputOrig = -1.0 + (inputTick / 2.0);

			// create the input layer

			for (int row = 0; row < inputEdgeSize; row++) {
				for (int col = 0; col < inputEdgeSize; col++) {
					SubstrateNode inputNode = result.createInputNode();
					inputNode.getLocation()[0] = -1;
					inputNode.getLocation()[1] = inputOrig + (row * inputTick);
					inputNode.getLocation()[2] = inputOrig + (col * inputTick);
				}
			}
			
			//Create the hidden layer
			
			for (int row = 0; row < inputEdgeSize; row++) {
				for (int col = 0; col < inputEdgeSize; col++) {
					SubstrateNode hiddenNode = result.createHiddenNode();
					hiddenNode.getLocation()[0] = 0;
					hiddenNode.getLocation()[1] = inputOrig + (row * inputTick);
					hiddenNode.getLocation()[2] = inputOrig + (col * inputTick);
					
					//Link to every input node
					for (SubstrateNode inputNode : result.getInputNodes()) {
						result.createLink(inputNode, hiddenNode);
					}
				}
			}

			// create the output layer (and connect to input layer)

			for (int orow = 0; orow < outputEdgeSize; orow++) {
				for (int ocol = 0; ocol < outputEdgeSize; ocol++) {
					SubstrateNode outputNode = result.createOutputNode();
					outputNode.getLocation()[0] = 1;
					outputNode.getLocation()[1] = outputOrig + (orow * outputTick);
					outputNode.getLocation()[2] = outputOrig + (ocol * outputTick);

					// link this output node to every hidden node
					for (SubstrateNode hiddenNode : result.getHiddenNodes()) {
						result.createLink(hiddenNode, outputNode);
					}
				}
			}

			return result;
		}
}
