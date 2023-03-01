package DTNRouting;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
//import java.io.*;

public class WriteFile {

	private String path;
	private boolean append_to_file = false;
	
	public WriteFile(String path_file, boolean append_value) {
		path = path_file;
		append_to_file = append_value;
	}
	
	public void clearTheFile() throws IOException {
		FileWriter write = new FileWriter(path, false);
		PrintWriter print_line = new PrintWriter(write, false);
		print_line.flush();
		print_line.close();
		write.close();
	}

	
	
	// User achieved priority,number_of_QualityMetricsMet,QoIT_Score of SELECTED SOURCE 
	public void writeToFile(long time, int simulation_part, int uavs, int destination, int source, double priority, int number_of_QualityMetricsMet, double QoIT_Score) throws IOException{
		FileWriter write = new FileWriter(path, append_to_file);
		PrintWriter print_line = new PrintWriter(write);
		print_line.printf("%d", time);
		print_line.printf(",");
		print_line.printf("%d", simulation_part);
		print_line.printf(",");
		print_line.printf("%d", uavs);
		print_line.printf(",");
		print_line.printf("%d", destination);
		print_line.printf(",");
		print_line.printf("%d", source);
		print_line.printf(",");
		print_line.printf("%f", priority);
		print_line.printf(",");
		print_line.printf("%d", number_of_QualityMetricsMet);
		print_line.printf(",");
		print_line.printf("%f", QoIT_Score);
		print_line.printf("\n");

		print_line.flush();
		print_line.close();
	}

	// User Requirements w.r.t Network Metrics Values
	public void writeToFile(long time, int simulation_part, int destination, double bandwidth, double hopcount, double path_integrity, double information_utility) throws IOException{
		FileWriter write = new FileWriter(path, append_to_file);
		PrintWriter print_line = new PrintWriter(write);
		print_line.printf("%d", time);
		print_line.printf(",");
		print_line.printf("%d", simulation_part);
		print_line.printf(",");
		print_line.printf("%d", destination);
		print_line.printf(",");
		print_line.printf("%f", bandwidth);
		print_line.printf(",");
		print_line.printf("%f", hopcount);
		print_line.printf(",");
		print_line.printf("%f", path_integrity);
		print_line.printf(",");
		print_line.printf("%f", information_utility);
		print_line.printf("\n");

		print_line.flush();
		print_line.close();
	}

	// Src-Dst available Network Metrics Values for SELECTED SOURCE
	// Prints: 1. NetworkMetricValues_SelectedSource; 2. NetworkMetricsScore_SelectedSource; 3. QualityMetricsScore_SelectedSource
	public void writeToFile(long time, int simulation_part, int destination, int source, double bandwidth, double hopcount, double path_integrity, double information_utility) throws IOException{
		FileWriter write = new FileWriter(path, append_to_file);
		PrintWriter print_line = new PrintWriter(write);
		print_line.printf("%d", time);
		print_line.printf(",");
		print_line.printf("%d", simulation_part);
		print_line.printf(",");
		print_line.printf("%d", destination);
		print_line.printf(",");
		print_line.printf("%d", source);
		print_line.printf(",");
		print_line.printf("%f", bandwidth);
		print_line.printf(",");
		print_line.printf("%f", hopcount);
		print_line.printf(",");
		print_line.printf("%f", path_integrity);
		print_line.printf(",");
		print_line.printf("%f", information_utility);
		print_line.printf("\n");

		print_line.flush();
		print_line.close();
	}

}


