import java.io.*;
import java.util.*;
import java.lang.String;

public class makeData{
	public static ArrayList<ArrayList<Double>> xs = new ArrayList<>();
	public static ArrayList<Double> ys = new ArrayList<>();
	
	public makeData(){

	}

	public static void normalize() {
      Double miny = ys.get(0);
      for (Double y : ys) {
         if (y < miny) miny = y;
      }
      for (int i=0; i<ys.size(); i++) {
         Double y = ys.get(i);
         y -= miny;
         ys.set(i , y);
      }
      Double maxy = ys.get(0);
      for (Double y : ys) {
         if (y > maxy) maxy = y;
      }
      for (int i=0; i<ys.size(); i++) {
         Double y = ys.get(i);
         y = y / maxy;
         ys.set(i , y);
      }
      
      //now xs
      for (int c = 0; c < xs.get(0).size(); c++) {
         Double min = xs.get(0).get(c);
         for (int i = 0; i < xs.size(); i++) {
            Double x = xs.get(i).get(c);
            if (x < min) min = x;
         }
         for (int i = 0; i < xs.size(); i++) {
            Double x = xs.get(i).get(c);
            x -= min;
            xs.get(i).set(c, x);
         }
         Double max = xs.get(0).get(c);
         for (int i = 0; i < xs.size(); i++) {
            Double x = xs.get(i).get(c);
            if (x > max) max = x;
         }
         for (int i = 0; i < xs.size(); i++) {
            Double x = xs.get(i).get(c);
            x = x / max;
            xs.get(i).set(c, x);
        }         
     	}
    }

	
	public static void main(String[] args) throws FileNotFoundException, IOException{

		Scanner scan = new Scanner(new File("higgs_training_set.csv"));
    	scan.useDelimiter(",");
    	while (scan.hasNextLine()) {  
        	String line = (scan.nextLine());
            if (line.contains((CharSequence)"?")){ continue;}
            String[] arr = line.split(",");
            ArrayList<Double> temp = new ArrayList<Double>();
            for (int i=0; i<arr.length; i++){
               Double dub;
               try { dub = Double.parseDouble(arr[i]); }
               catch (NumberFormatException nfe) { continue; }
               if (i == 0){
                   ys.add(dub);
               }
               else{   
                   temp.add(dub);
               }
            }
            xs.add(temp);
      	} 
     	scan.close();

     	normalize();



     	File fout = new File("Higgs2.data");
     	FileOutputStream fos  = new FileOutputStream(fout);

     	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

     	int header1 = xs.size();
     	int header2 = xs.get(0).size()-1;
     	int header3 = 1;

     	bw.write(header1+" "+header2+ " "+header3+ "\n");
     	int count = 0;
     	for(ArrayList<Double> vector : xs){
     		for(Double feature : vector){
	     		bw.write(feature+ " ");
     		}
     		bw.write(1.0+"");
     		bw.write("\n");
     		bw.write(String.valueOf(ys.get(count)));
     		bw.write("\n");
     		count++;
     	}
     	bw.flush();

	}
}