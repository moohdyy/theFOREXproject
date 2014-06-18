package hneat;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.hyperneat.substrate.Substrate;
import org.encog.neural.hyperneat.substrate.SubstrateFactory;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.species.OriginalNEATSpeciation;
import org.encog.util.Format;

public class Main {

	
	public static void main(String[] args) {
		
		Manager mgr = new Manager();
		Substrate substrate = NewSubstrateFactory.factorHLSubstrate(49,49);
		CandlestickScore score = new CandlestickScore();
		mgr.pop = new NEATPopulation(3,1,300);
		mgr.pop.setActivationCycles(4);
		mgr.pop.reset();
		mgr.train = NEATUtil.constructNEATTrainer(mgr.pop,score);
		OriginalNEATSpeciation speciation = new OriginalNEATSpeciation();
		speciation.setCompatibilityThreshold(1);
		mgr.train.setSpeciation(speciation = new OriginalNEATSpeciation());
		
//		NEATPopulation network = null;
//		try {			
//			InputStream is = new FileInputStream("HNEAT49-EURUSD1-pop16");
//			ObjectInputStream ois = new ObjectInputStream(is);
//			network = (NEATPopulation)ois.readObject();
//			ois.close();
//			is.close();
//		} catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	Random rand = new Random();
//	for(int z=0;z<100;z++)
//	{
//		int caseNo = rand.nextInt(16555)+1;
//		File file = new File("C:\\Users\\Maarten-work\\AppData\\Roaming\\MetaQuotes\\Terminal\\31B0DF029ADD8811005FA69713AE130D\\MQL4\\Files\\Data\\EURUSD1.csv_Pieces\\EURUSD1_"+caseNo+".csv");
//		List<List<Double>> csvData = CSV.processCSVSorted(file);
//		int vRes = 50;		
//		MLData input = new BasicMLData(csvData.size()*vRes);
//		
//		//Find global low and high
//		double min = 1000000.0;
//		double max = 0.0;
//		for (int i=0;i<csvData.size();i++) {
//			if(csvData.get(i).get(0)<min) {
//				min = csvData.get(i).get(0);
//			}
//			if(csvData.get(i).get(1)>max) {
//				max = csvData.get(i).get(1);
//			}
//		}
//		
//		//Create the input image
//		for (int i=0; i<csvData.size();i++) {
//			for (int j=0; j<vRes; j++) {
//				int index = i*vRes+j;
//				if(((max-min)/vRes * j > (csvData.get(i).get(2)-min)) && ((max-min)/vRes * j < (csvData.get(i).get(3)-min))) {    //Test if between low and high ending
//					input.setData(index,1.0);
//				} else if(((max-min)/vRes * j > (csvData.get(i).get(0)-min)) && ((max-min)/vRes * j < (csvData.get(i).get(1)-min))) {    //Test if between period low and high
//					input.setData(index,0.0);
//				} else {
//					input.setData(index,-1.0);
//				}
//			}
//		}
//		MLMethod phenotype = network;
//		
//		MLData output = ((NEATPopulation)phenotype).compute(input);
//		
//		double output1 = output.getData(0);
//		double output2 = output.getData(1);
//		System.out.println("Outputs: "+caseNo+":"+output1+";"+output2);
//	}
//		
//		System.exit(0);
		
		int it = 0;
		while(it<100) {
			mgr.train.iteration();
			System.out.println(Format.formatDouble(mgr.train.getError(), 2));
			System.out.println(Format.formatInteger(mgr.train
					.getIteration()));
			System.out.println(Format.formatInteger(mgr.pop
					.getSpecies().size()));
			
			try {
				FileOutputStream out = new FileOutputStream("generatedNetworks\\HNEAT49-EURUSD60-pop"+it);
				ObjectOutputStream oos = new ObjectOutputStream(out);
				oos.writeObject(mgr.pop);
				oos.close();
				out.close();
			} catch (FileNotFoundException e) {
				System.out.println("Error: file not found");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error: IOException");
				e.printStackTrace();
			}
			
			it++;
		}
	}

}
