import java.io.*;
import java.util.*;
import java.lang.String;

public class makeData{
	public static ArrayList<ArrayList<Double>> xs = new ArrayList<>();
	public static ArrayList<ArrayList<Double>> ys = new ArrayList<>();
	
	public makeData(){

	}

	
	public static void main(String[] args) throws FileNotFoundException, IOException{

		Scanner scan = new Scanner(new File(args[0]));
    	//scan.useDelimiter("\\*");
    	while (scan.hasNextLine()) {  
        	String line = (scan.nextLine());
        	//System.out.println(line);
            if (line.contains((CharSequence)"?")){ continue;}
            
            String[] input_output = line.split("\\*");
            String input_vector = input_output[0];
            String output_vector = input_output[1];

            input_vector = input_vector.replaceAll("\\[","").replaceAll("\\]","");
            output_vector = output_vector.replaceAll("\\[","").replaceAll("\\]",""); 

            String[] xArr = input_vector.split(",");
            String[] yArr = output_vector.split(",");

            ArrayList<Double> tempx = new ArrayList<>();
            ArrayList<Double> tempy = new ArrayList<>();

            for(int i = 0; i<xArr.length;i++){
            	Double dub;
            	dub = Double.parseDouble(xArr[i]);
            	tempx.add(dub);
            }

            for(int j=0;j<yArr.length;j++){
            	Double dub;
            	dub = Double.parseDouble(yArr[j]);
            	tempy.add(dub);
            }

            xs.add(tempx);
            ys.add(tempy);

    	} 
     	scan.close();


     	File fout = new File(args[1]);
     	FileOutputStream fos  = new FileOutputStream(fout);

     	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

     	int header1 = xs.size();
     	int header2 = xs.get(0).size();
     	int header3 = ys.get(0).size();

     	bw.write(header1+" "+header2+ " "+header3+ "\n");


     	for(int count=0; count<xs.size();count++){
     		ArrayList<Double> xvector = xs.get(count);
     		ArrayList<Double> yvector = ys.get(count);
     		for(Double feature:xvector){
     			bw.write(feature+" ");
     		}
     		bw.write("\n");
     		for(Double feature:yvector){
     			bw.write(feature+" ");
     		}
     		bw.write("\n");     		
     	}
     	
     	bw.flush();

	}
}