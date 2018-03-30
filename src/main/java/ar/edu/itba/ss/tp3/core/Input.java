package ar.edu.itba.ss.tp3.core;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Input {
	
	static List<MassiveParticle> particles = new ArrayList<MassiveParticle>();

	public Input (String dynamicFilePath) {

		try {
			
			File dynamicFile = new File(dynamicFilePath);
			
			try {
				Scanner dynamicRead = new Scanner(dynamicFile);
				
				dynamicRead.next(); // avoid t0 in Dynamic File
			
				while(dynamicRead.hasNext()){
					particles.add(new MassiveParticle(
							Double.parseDouble(dynamicRead.next()), // x
							Double.parseDouble(dynamicRead.next()), // y
							Double.parseDouble(dynamicRead.next()), // radius
							Double.parseDouble(dynamicRead.next()), // vx
							Double.parseDouble(dynamicRead.next()), // vy
							Double.parseDouble(dynamicRead.next())) // mass
					);
				}
				
				dynamicRead.close();			
			} catch (Exception e) {
				System.out.println("Error scanning file");
			}
			
		} catch (Exception e) {
			System.out.println("Error opening or finding file");
		}
		
	}
	
	public static List<MassiveParticle> getParticles() {
		return particles;
	}
	
	
	
}
