package architect;

import processing.core.PApplet;
import processing.core.PVector;

import java.lang.Math;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import architect.*;
import controlP5.*;

public class Architect extends PApplet {

	ControlP5 cp5;

	Textarea consoleArea;
	
	Println console;
	
	float CHARGE;
	float MAXVEL;
	float MAXRAD;
	float BASELINE;
	float WALLMULTIPLIER;
	int TRACEDEPTH;
	int TRACESTEP;
	float PUSHBACK;
	int N;
	float MU;
	float GEN;
	boolean TWODIMEN;
	float INDHUE;
	boolean LOOPING;
	boolean MUTATION;
	boolean TRACING;
	boolean DYNAMICS;
	boolean STATISTICS;
	boolean HELP;
	boolean FRATE;
	boolean GRAYBG;
	float BG;
	float OUTLINE;

	Population population;

	public void setup() {
		cp5 = new ControlP5(this);
		cp5.enableShortcuts();
		
		frame.setTitle("Architect - Adaptive Differential Evolution Global Optimization Algorithm");
		TWODIMEN = false;
		MUTATION = false;
		TRACING = true;
		DYNAMICS = true;
		STATISTICS = true;
		HELP = true;
		FRATE = false;
		GRAYBG = true;

		CHARGE = 30; // 50
		MAXVEL = 2; // 2
		MAXRAD = 6;
		BASELINE = 25;
		WALLMULTIPLIER = 20;
		TRACEDEPTH = 50; // 300
		TRACESTEP = 16; // 20
		PUSHBACK = (float) 0.75;
		
		if (!GRAYBG) {
			BG = 20;
			OUTLINE = 100;
		}
		else {
			BG = 100;
			OUTLINE = 20;
		}
		
		N = 10;
		MU = (float) 0.1;
		GEN = (float) 60.0;			// frames per generation
		
		INDHUE = 95;
		LOOPING = true;
		
		size(1000, 600);
//		size(600,400);
		colorMode(HSB,100);
		smooth();
		noStroke();
		population = new Population();	// begins with a single individual
		
		consoleArea = cp5.addTextarea("txt")
                  .setPosition(width-250, 50)
                  .setSize(200, 300)
                  .setFont(createFont("", 10))
                  .setLineHeight(14)
                  .setColor(color(0))
                  .setColorBackground(color(0, 100))
                  .setColorForeground(color(255, 100));
		
		console = cp5.addConsole(consoleArea);
		println("Console initiated");
		//fontN = loadFont("GillSans-48.vlw");
//		fontI = loadFont("GillSans-Italic-48.vlw");
		
	}

	public void draw() {
		background(0,0,BG);
		population.run();
		if (STATISTICS) { stats(); }
		if (HELP) { help(); }
		if (FRATE) { showFrameRate(); }
		console.play();
	}

	void showFrameRate() {
		fill(0,0,OUTLINE);
		stroke(0,0,OUTLINE);
		//textFont(fontN, 16);
		int fps = (int) (frameRate);
		text(fps + " frames / sec", 10, 85);
	}

	void help() {
		
		fill(0,0,OUTLINE);
		stroke(0,0,OUTLINE);
		smooth();
		//textFont(fontN, 16);

		float h = 120;
		float mod = 20;
		text("H",10,h); text("-  show/hide keyboard commands",70,h); h += mod;
		text("F",10,h); text("-  show/hide frame rate",70,h); h += mod;
		text("S",10,h); text("-  show/hide statistics",70,h); h += mod;
		//text("T",10,h); text("-  show/hide tracing",70,h); h += mod;
		//text("C",10,h); text("-  switch between background colors",70,h); h += mod;
		text("SPACE",10,h); text("-  start/stop animation",70,h); h += mod;
		text("D",10,h); text("-  start/stop population dynamics",70,h); h += mod;
		text("M",10,h); text("-  start/stop mutation /// Adaptive Differential Evolution Global Optimization Algorithm",70,h); h += mod;
		//text("2",10,h); text("-  switch between 1 and 2 dimensions",70,h); h += mod;
		text("DOWN",10,h); text("-  decrease population size",70,h); h += mod;
		text("UP",10,h); text("-  increase population size",70,h); h += mod;
		text("LEFT",10,h); text("-  decrease generation time",70,h); h += mod;
		text("RIGHT",10,h); text("-  increase generation time",70,h); h += mod;
		text("<",10,h); text("-  decrease trace rate",70,h); h += mod;
		text(">",10,h); text("-  increase trace rate",70,h); h += mod;
		text("CLICK",10,h); text("-  add migrant to population",70,h); h += mod;
			
		//textFont(fontN, 12);
		text("Copyright 2013 Mahmut Bulut",width-200,20);
		
	}

	void stats() {

		fill(0,0,OUTLINE);
		stroke(0,0,OUTLINE);
		//textFont(fontN, 16);

		// population size
		text(N + " individuals",10,25);

		// generation time
		if (DYNAMICS) {
			double grate = round( (float)(( (float) GEN / (float) frameRate ) * 10.0) ) / 10.0;
			text(grate + " sec / gen", 10, 45);
		}
		
		// trace time
		if (!TWODIMEN && TRACING) {
			int trate = (round(frameRate * PUSHBACK));
			text(trate + " pixels / sec", 10, 65);	
		}
		
		// intervals
		if (!TWODIMEN && DYNAMICS && TRACING) {
			//textFont(fontN, 12);
			float t = 0;		
			if (N>0) { line(width-34,height-BASELINE,width-10,height-BASELINE); }
			for (int k = N; k > 1; k--) {
				float mod = coalInterval(k);
				if (mod > 10) { 
					if (k < 10) { text(k,width-24,height-t-mod/2-BASELINE+5); }
					else { text(k,(float) (width-27.5),height-t-mod/2-BASELINE+5); }
				}
				t += mod;
				line(width-34,height-t-BASELINE,width-10,height-t-BASELINE);	
			}
		}
		
	}

	// Add a new individual into the population
	public void mousePressed() {
		population.die();
		population.addIndividual(new Individual(new PVector(mouseX,mouseY)));
	}

	public void keyPressed() {
		if (key == ' ') {
			if (LOOPING) {
				LOOPING = false;
				noLoop();
			}
			else if (!LOOPING) {
				LOOPING = true;
				loop();
			}
	  	} 
	  	if (key == '2') {
			if (TWODIMEN) { 
				population.resetTrace();
				TWODIMEN = false; 
			}
			else if (!TWODIMEN) { 
				population.resetTrace();
				TWODIMEN = true; 
			}
	  	} 
	  	if (key == 'm') {
			if (MUTATION) { MUTATION = false; }
			else if (!MUTATION) { MUTATION = true; }
	  	}   
	  	if (key == 't') {
			if (TRACING) { TRACING = false; }
			else if (!TRACING) { TRACING = true; }
	  	} 
	  	if (key == 'd') {
			if (DYNAMICS) { DYNAMICS = false; }
			else if (!DYNAMICS) { DYNAMICS = true; }
	  	}  
	  	if (key == 's') {
			if (STATISTICS) { STATISTICS = false; }
			else if (!STATISTICS) { STATISTICS = true; }
	  	}    
	  	if (key == 'h') {
			if (HELP) { HELP = false; }
			else if (!HELP) { HELP = true; }
	  	}      	
	  	if (key == 'f') {
			if (FRATE) { FRATE = false; }
			else if (!FRATE) { FRATE = true; }
	  	}      	  	
		if (keyCode == UP) { 
			population.replicate();
			N++;
	  	} 
		if (keyCode == DOWN) {
			boolean success = population.die();
			if (success) { N--; }
	  	}   
		if (keyCode == RIGHT) { 
			GEN += 1.0;
	  	} 
		if (keyCode == LEFT) { 
			GEN -= 1.0;
	  	}   
		if (key == '.') { 
			PUSHBACK += 0.01;
	  	}  
		if (key == ',') { 
			PUSHBACK -= 0.01;
	  	}  
//		if (key == 'p') { 
//			save("image.png");
//	  	}   	
		if (key == 'c') {
			if (GRAYBG) {
				GRAYBG = false;
				BG = 100;
				OUTLINE = 20;
			}
			else if (!GRAYBG) {
				GRAYBG = true;
				BG = 20;
				OUTLINE = 100;
			}
	  	}   	
	}

	class Individual {

		PVector loc;
		PVector vel;
		PVector acc;
		float r;  // radius
		boolean growing;
		boolean dying;
		float hue;
		LinkedList trace;

		Individual(PVector l) {
			loc = l.get();
	    	vel = new PVector(0,0);
	    	acc = new PVector(0,0);
	    	r = (float) 0.001;
	    	growing = true;
	    	dying = false;
	    	
	    	if (MUTATION) { hue = random(0,100); }
	  		else { hue = INDHUE; }
	  		
	  		if (!TWODIMEN) {
				loc.y = height-BASELINE;					// constrains to horizontal line	
			}
	  
	    	trace = new LinkedList();
	    	for (int i = 0; i < TRACEDEPTH; i++) {
	    		PVector tl = new PVector(loc.x,loc.y,hue);
	    		trace.add(tl);
	    	}
		}
		
		Individual(PVector l, Float h, LinkedList array) {
			loc = l.get();
	    	vel = new PVector(0,0);
	    	acc = new PVector(0,0);
	    	r = (float) 0.001;
	    	growing = true;
	    	dying = false;
	    	hue = h;
	    	trace = new LinkedList();
	    	for (int i = 0; i < array.size(); i++) {
	    		PVector tl = (PVector) array.get(i);
	    		float x = tl.x;
	    		float y = tl.y;
	    		float z = tl.z;
	    		trace.add(new PVector(x,y,z));
	    	}
		}	
	    
		void update() {
			vel.add(acc);          						// update velocity
			vel.x = constrain(vel.x,-MAXVEL,MAXVEL);	// contrains speed
			vel.y = constrain(vel.y,-MAXVEL,MAXVEL);
			
			loc.add(vel);          						// update location
			
			if (!TWODIMEN) {
				loc.y = height-BASELINE;				// constrains to horizontal line	
			}
			
			if (growing) { r = (float) (r + 0.9); }
			if (r > 1.3*MAXRAD) { growing = false; }
			if (r > MAXRAD) { r = (float) (r - 0.4); }
			if (dying) { r = (float) (r - 0.4); }
			
			reset();
			
			// dynamically calculate TRACESTEP
			TRACESTEP = (int) (( (float)height / (float) (TRACEDEPTH * PUSHBACK) ) + 1);
			if (frameCount % TRACESTEP == 0) {
				extendTrace();
			}
			
		}
		
		void reset() {
			vel = new PVector(0,0);
			acc = new PVector(0,0);
		}
		
		void extendTrace() {
	    	PVector tl = new PVector(loc.x,loc.y,hue);
	    	trace.add(tl);
	    	trace.remove();
		}
		
		void resetTrace() {
		    trace = new LinkedList();
	    	for (int i = 0; i < TRACEDEPTH; i++) {
	    		PVector tl = new PVector(loc.x,loc.y,hue);
	    		trace.add(tl);
	    	}
		}
		
		void mutate(double data) {
			//hue = random(0,100);
			if(hue >= 100)
				hue = 0;
			else
				hue = 0+(int)data;
		}
	  
		void display() {
	    	if (TRACING) { displayTrace(); }
			displayInd();
	  	}
	  	
	  	void displayTrace() {
	  	    // draw tail on each individual
	    	float tempx = loc.x;
	    	float tempy = loc.y;
	    	float temph = hue;
			ListIterator itr = trace.listIterator(TRACEDEPTH);
			while (itr.hasPrevious()) {
	   			PVector tl = (PVector) itr.previous();
	    		if (!TWODIMEN) {
	    			tl.y = tl.y - PUSHBACK;
	    		}
	    		stroke(tl.z,90,100);
	    		line(tempx, tempy, tl.x, tl.y);
	    		tempx = tl.x;
	    		tempy = tl.y;
	    	}
	  	}
	  	
	  	void displayInd() {
	  	    // draw a circle for each individual
			fill(hue,90,100); // 223,227,197
	    	stroke(0,0,OUTLINE);
	    	ellipse(loc.x, loc.y, r*2, r*2);
	  	}
	  	
	}

	// The Population (a list of Individual objects)
	class Population {
	  
	  	ArrayList pop; // An arraylist for all the individuals

	  	Population() {
	    	pop = new ArrayList(); 
	    	for (int i=0; i < N; i++) {
	    		float w = random(0,width);
	    		float h = random(0,height);
	    		if (!TWODIMEN) {
	    			h = height-BASELINE;
	    		}
	    		pop.add(new Individual(new PVector(w,h)));
	    	}
	  	}

		void run() {
			
			if (DYNAMICS) { splitstep(); }
			if (MUTATION) { mutate(); }
			repulsion();
			update();
			exclusion();
			cleanup();
			display();
			
		}
		
		int size() {
			return pop.size();
		}

		void addIndividual(Individual ind) {
			pop.add(ind);
		}

		void replicate() {
			if (pop.size() > 0) {
				int rand = (int) (random(0,pop.size()));
				Individual ind = (Individual) pop.get(rand);
				float newx = ind.loc.x + random(-1,1);
				float newy = ind.loc.y + random(-1,1);
				pop.add(new Individual(new PVector(newx,newy), ind.hue, ind.trace ));
				
			}
			else {
				float w = width/2 + random(-1,1);
				float h = height/2 + random(-1,1);
				pop.add(new Individual(new PVector(w,h)));
			}
			
		}
		
		boolean die() {					// return true if successful
			boolean success = false;
			// how many are not dying
			int livecount = 0;
			for (int i = 0; i < pop.size(); i++) {
				Individual ind = (Individual) pop.get(i); 
				if (!ind.dying) {
					livecount++;
				}
			}
			if (livecount > 0) {
				int rand = (int) (random(0,pop.size()));
				Individual ind = (Individual) pop.get(rand);
				while (ind.dying) {									// pick another
					rand = (int) (random(0,pop.size()));
					ind = (Individual) pop.get(rand);
				}
				ind.dying = true;
				ind.growing = false;
				success = true;
			}
			return success;
		}
		
		void splitstep() {									// called once per frame
			float popBD = (1 / (float)GEN) * (float) N;		// population birth-death rate
			int events = poissonSample(popBD);	
			for (int i = 0; i < events; i++) {
				die();
				replicate();
			}
		}
		
		void mutate() {
			// examplanary call is like this:
			// (1/(float)GEN) * (float)N * MU;
			/*

			(defn -mutator
					  ;; int int int int
					  [size lowerbound upperbound maxiter]
					  (println "Population size: " size "LowerBound: " lowerbound "UpperBound: " upperbound "Maxiter: " maxiter)
					  (engine/optimized fitness-funct 0.2 0.1 size 10 lowerbound upperbound maxiter)
					  )
					  
					  */
			// Clojure calls
			
			
			float popMu = core.calfitness(GEN, N, MU);
			//float popMu = (1/(float)GEN) * (float)N * MU;
			
			int events = poissonSample(popMu);
			for (int i = 0; i < events; i++) {
				int rand = (int) (random(0,pop.size()));
				Individual ind = (Individual) pop.get(rand);
				double ax = core.mutator(pop.size(), -100, 100, 2);
				println("Mutation rate: "+ax);
				ind.mutate(ax*100);
			}
		}

		void cleanup() {
			for (int i = 0; i < pop.size(); i++) {
				Individual ind = (Individual) pop.get(i);  
				if (ind.r < 0) { 
					pop.remove(i);
					i = 0;
				}
			}
		}

		void update() {
			for (int i = 0; i < pop.size(); i++) {
				Individual ind = (Individual) pop.get(i);  
				ind.update(); 
			}
		}
		
		void resetTrace() {
			for (int i = 0; i < pop.size(); i++) {
				Individual ind = (Individual) pop.get(i);  
				ind.resetTrace(); 
			}
		}
		
		void display() {
			if (TRACING) {
				for (int i = 0; i < pop.size(); i++) {
					Individual ind = (Individual) pop.get(i);  
					ind.displayTrace(); 
				}
			}
			for (int i = 0; i < pop.size(); i++) {
				Individual ind = (Individual) pop.get(i);  
				ind.displayInd(); 
			}
		}
		
		void exclusion () {
			
			for (int i = 0 ; i < pop.size(); i++) {
			
				Individual ind = (Individual) pop.get(i);
				
				// freeze on contact with other individuals
		/*		for (int j = 0 ; j < pop.size(); j++) {
					if (i != j) {
						Individual jnd = (Individual) pop.get(j);
						float overlap = ind.r + jnd.r - PVector.dist(ind.loc,jnd.loc);
						if (overlap > 0) {
							ind.reset();
							jnd.reset();
						}
					}
				}
		*/		
				// exclude from walls
				ind.loc.x = constrain(ind.loc.x, ind.r*2, width-ind.r*2);
				ind.loc.y = constrain(ind.loc.y, ind.r*2, height-ind.r*2);
						
			}
			
	  	}

		void repulsion () {
			
			for (int i = 0 ; i < pop.size(); i++) {
			
				Individual ind = (Individual) pop.get(i);
				PVector push = new PVector(0,0);
				float distance;
				PVector diff;
				
				// repel from other Individuals
				for (int j = 0 ; j < pop.size(); j++) {
					if (i != j) {
				
						Individual jnd = (Individual) pop.get(j);
						// Calculate vector pointing away from neighbor
						diff = PVector.sub(ind.loc,jnd.loc);
						diff.normalize();
						// weight by Coulomb's law
						distance = PVector.dist(ind.loc,jnd.loc);
						diff.mult( coulomb(distance) );
						push.add(diff);
					
					}
				}
				
				// repel from left wall
				diff = new PVector(1,0);
				distance = ind.loc.x-0;
				diff.mult( WALLMULTIPLIER*coulomb(distance) );
				push.add(diff);

				// repel from right wall
				diff = new PVector(-1,0);
				distance = width-ind.loc.x;
				diff.mult( WALLMULTIPLIER*coulomb(distance) );
				push.add(diff);		
				
				// repel from top wall
				diff = new PVector(0,1);
				distance = ind.loc.y-0;
				diff.mult( WALLMULTIPLIER*coulomb(distance) );
				push.add(diff);

				// repel from bottom wall
				diff = new PVector(0,-1);
				distance = height-ind.loc.y;
				diff.mult( WALLMULTIPLIER*coulomb(distance) );
				push.add(diff);					
					
				// forces accelerate the individual			
				ind.acc.add(push);
				
			}
			
	  	}

	}

	float coulomb(float d) {
		float force;
		if (d > 0) {
			force = sq(CHARGE) / sq(d);
		}
		else {
			force = 10000;
		}
		return force;
	}

	int poissonSample(float lambda) {
		float t = exp(-1*lambda);
		int k = 0;
		float p = 1;
		while (p > t) {
			k++;
			p *= random(0,1);
		}
		return k - 1;
	}

	// coalescent intervals calculation, returns number of pixels in interval of k lineages
	// every frame each member of trace is decremented by PUSHBACK pixels
	float coalInterval(int k) {

		// Moran model with overlapping generations, k concurrent lineages
		float cof = (float) (2.0 / (float) ( (k-1) * k)); 
		float generations = (float) (cof * (float) N * 0.5);
		// converting from generations to frames
		float frames = generations * GEN;
		// converting from frames to pixels
		float pixels = frames * PUSHBACK;
		
		return pixels;
	}
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { architect.Architect.class.getName() });
	}
}
