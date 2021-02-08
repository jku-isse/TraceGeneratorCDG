package mainPackage;

import evaluation.Logger;
import evaluation.Seeder;
import model.Clazz;
import model.ClazzRTMCellList;
import model.Definitions;
import model.Method;
import model.MethodRTMCell;
import model.MethodRTMCellList;
import model.PredictionPattern;
import model.RTMCell;
import model.RTMCell.TraceValue;
import model.RTMCellList;
import model.Requirement;
import model.Variable;
import model.VariableList;
import model.VariableRTMCell;
import traceRefiner.TraceRefiner;
import traceRefiner.TraceRefinerPredictionPattern;
import traceValidator.TraceValidator;
import traceValidator.TraceValidatorPredictionPattern;
import traceValidatorGhabi.TraceValidatorGhabi;
import traceValidatorGhabi.TraceValidatorGhabiPredictionPattern;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class TraceProcessor {

	public static enum Algorithm {ValidatorSingle, ValidatorCallTypes, ValidatorIterations, GhabiValidator, Refiner, 
		IncompletenessSeederT, IncompletenessSeederN, IncompletenessSeederTN, ErrorSeederT, ErrorSeederN, ErrorSeederTN, seedingTest1, seedingTest2, VSM,LSI};
    private static long startTime = System.currentTimeMillis();
    public static Algorithm test = Algorithm.ValidatorSingle;
    public static List<JSONObject> jsonArray = new  ArrayList<JSONObject> (); 
    public static LinkedHashMap<String, LinkedHashMap<String, String>> variableStorageTraces = new LinkedHashMap<String, LinkedHashMap<String, String>>();  
    
	static public void main(String[] args) throws Exception {
//		 jsonfileWriter = new FileWriter("C:\\Users\\mouna\\git\\TraceProcessor\\log\\percentages\\IncompletenessT.json");
		 //tests
		//more validation patterns innner/outer Tt-x-n, also multiple callers (meaning that is one outer and one inner caller with T), single callers T*N-x-N (meaning are are more than one caller with T and a single N)
		//validation with mutliple iterations
		//validation with weak and strong traces - gray zone wiht likely/unlikely traces
		//validation with seeding
		variableStorageTraces.put("chess", new LinkedHashMap<String, String>());
		variableStorageTraces.put("gantt", new LinkedHashMap<String, String>());
		variableStorageTraces.put("itrust", new LinkedHashMap<String, String>());
		variableStorageTraces.put("jhotdraw", new LinkedHashMap<String, String>());

		//*********************************
		boolean runAllTests = false;
		ArrayList<String> programs = new ArrayList<String>();
		for(int i=0; i<1; i++) {
			programs.add("chess");
			programs.add("gantt");
			programs.add("itrust");
			programs.add("jhotdraw");
//			programs.add("vod");
			
	}
		
		
		 Collections.sort(programs); 
//		programs.add("jhotdraw");
		if (test==Algorithm.seedingTest1) {
			
		
		DatabaseInput.read(programs.get(1));
		
	
	
		
		System.out.println("Requirement; x; y; z; percentageZ z/(x+y+z)");
		for(Requirement req: Requirement.requirementsHashMap.values()) {
			MethodRTMCellList mylist = req.getRTMMethodCellList(); 
			int x=0; int y=0; int z=0; 

			for(RTMCell elem: mylist) {
				if(elem.getSubjectN()==0 && elem.getSubjectT()>0) {
					y++; 
				}
				else if(elem.getSubjectT()==0 && elem.getSubjectN()>0) {
					x++; 
				}
				else if(elem.getSubjectT()>0 && elem.getSubjectN()>0) {
					z++; 
				}
			}
			double res=(double)z/(x+y+z)*100; 
		
			int percentageZ= (int) Math.round(res); 
			System.out.println(req.ID+";"+x+";"+y+";"+z+";"+percentageZ);
		}
		}
		if (test==Algorithm.seedingTest2) {
			
			
		DatabaseInput.read(programs.get(3));
		System.out.println("Requirement; x; y; z; percentageControversial (x+z)/(x+y+z)");
		for(Requirement req: Requirement.requirementsHashMap.values()) {
			MethodRTMCellList mylist = req.getRTMMethodCellList(); 
			int x=0; int y=0; int z=0; 

			for(RTMCell elem: mylist) {
				if(elem.getSubjectN()==0 && elem.getSubjectT()>0 && elem.getGoldTraceValue()==TraceValue.Trace) {
					y++; 
				}
				else if(elem.getSubjectT()==0 && elem.getSubjectN()>0 && elem.getGoldTraceValue()==TraceValue.Trace) {
					x++; 
				}
				else if(elem.getSubjectT()>0 && elem.getSubjectN()>0 && elem.getGoldTraceValue()==TraceValue.Trace) {
					z++; 
				}
			}
			double res=(double)(x+z)/(x+y+z)*100; 
		
			int percentageXZ= (int) Math.round(res); 
			System.out.println(req.ID+";"+x+";"+y+";"+z+";"+percentageXZ);
		}
		}
		//*********************************
if (test==Algorithm.ErrorSeederT ||test==Algorithm.ErrorSeederN || test==Algorithm.ErrorSeederTN) {
	// validator test single
	int iteration=0; 
	String type=""; 
	long seed=0; 
	String lastprogram=""; 
	List<Long> mySeeds = new ArrayList<>(); 
	for (String program : programs) {
		
		if(lastprogram.equals(program)) {
			iteration++; 
		}
		else {
			iteration=1; 
			Logger.writePercentagestoJSONfile( type, iteration, lastprogram, mySeeds); 
			mySeeds = new ArrayList<>(); 

		}
		if(test==Algorithm.ErrorSeederT) {
			type="ErrorT"; 
		}
		if(test==Algorithm.ErrorSeederN) {
			type="ErrorN"; 
		}
		if(test==Algorithm.ErrorSeederTN) {
			type="ErrorTN"; 
		}
		//call method that read json file get random - input: iteration num , programname
		seed=Seeder.readJSON(iteration, program, type);	
		mySeeds.add(seed); 
	Random generator = new Random(seed);
	Definitions.callerType = Definitions.CallerType.extended;
	int version = 2;
	
	TraceValidatorPredictionPattern.define(version);
	Logger.logPatternsReset(TraceValidatorPredictionPattern.patterns);

	DatabaseInput.read(program);
	Seeder.seedInputMethodTraceValuesWithDeveloperGold();
	Seeder.seedInputClazzTraceValuesWithDeveloperGold();
	Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();

	if(test==Algorithm.ErrorSeederTN) {
		Seeder.seedInputMethodRTM(false, false, true, true, program, type, generator, iteration);
	
	}
	if(test==Algorithm.ErrorSeederT) {

		Seeder.seedInputMethodRTM(false, false, true, false, program, type, generator, iteration);
	}
	if(test==Algorithm.ErrorSeederN) {

		Seeder.seedInputMethodRTM(false, false, false, true, program, type, generator, iteration);
	}
	

	TraceProcessor.validate(program, "val-v" + 2 + "-" + Definitions.callerType.toString());
	Logger.logPatternsSeeding(program, TraceValidatorPredictionPattern.patterns, type);
	System.out.println("==================== "+iteration);

	System.out.println("HERE1");

	lastprogram=program; 
}
	Logger.writePercentagestoJSONfile( type, iteration, lastprogram, mySeeds); 
}
		
		
		if (test==Algorithm.IncompletenessSeederN|| test==Algorithm.IncompletenessSeederT|| test==Algorithm.IncompletenessSeederTN) {
			// validator test single
			int iteration=0; 
			String type=""; 
			long seed=0; 
			String lastprogram=""; 
			List<Long> mySeeds = new ArrayList<>(); 
			for (String program : programs) {
				
				if(lastprogram.equals(program)) {
					iteration++; 
				}
				else {
					iteration=1; 
					Logger.writePercentagestoJSONfile( type, iteration, lastprogram, mySeeds); 
					mySeeds = new ArrayList<>(); 

				}
				if(test==Algorithm.IncompletenessSeederTN) {
					type="IncompletenessTN"; 
				}
				if(test==Algorithm.IncompletenessSeederT) {
					type="IncompletenessT"; 
				}
				if(test==Algorithm.IncompletenessSeederN) {
					type="IncompletenessN"; 
				}
				//call method that read json file get random - input: iteration num , programname
				seed=Seeder.readJSON(iteration, program, type);	
				mySeeds.add(seed); 
			Random generator = new Random(seed);
			Definitions.callerType = Definitions.CallerType.extended;
			int version = 2;
			
			TraceValidatorPredictionPattern.define(version);
			Logger.logPatternsReset(TraceValidatorPredictionPattern.patterns);

			DatabaseInput.read(program);
			Seeder.seedInputMethodTraceValuesWithDeveloperGold();
			Seeder.seedInputClazzTraceValuesWithDeveloperGold();
			Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
		
			if(test==Algorithm.IncompletenessSeederTN) {
				Seeder.seedInputMethodRTM(true, true, false, false, program, type, generator, iteration);
			
			}
			if(test==Algorithm.IncompletenessSeederT) {

				Seeder.seedInputMethodRTM(true, false, false, false, program, type, generator, iteration);
			}
			if(test==Algorithm.IncompletenessSeederN) {

				Seeder.seedInputMethodRTM(false, true, false, false, program, type, generator, iteration);
			}
			

			TraceProcessor.validate(program, "val-v" + 2 + "-" + Definitions.callerType.toString());
			Logger.logPatternsSeeding(program, TraceValidatorPredictionPattern.patterns, type);
			System.out.println("==================== "+iteration);
		
			System.out.println("HERE1");

			lastprogram=program; 
}
			Logger.writePercentagestoJSONfile( type, iteration, lastprogram, mySeeds); 
		}
		if (test==Algorithm.ValidatorSingle) {
			// validator test single
			
			
			
		for(String program: programs) {
			int version = 2;
			Definitions.callerType = Definitions.CallerType.extended;

//			TraceValidatorPredictionPattern.define(version);
//			Logger.logPatternsReset(TraceValidatorPredictionPattern.patterns);

			DatabaseInput.read(program);
		
//			Seeder.seedInputMethodTraceValuesWithDeveloperGold();
//			Seeder.seedInputClazzTraceValuesWithDeveloperGold();
//			Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
//			validate(program, "val-v" + version + "-" + Definitions.callerType.toString());
//			Logger.logPatterns(program, TraceValidatorPredictionPattern.patterns, "val-v" + version);
//			Logger.logPatternsSeeding(program, TraceValidatorPredictionPattern.patterns, "NoSeeding"+program);

		}
			

		}

		if (runAllTests || test==Algorithm.ValidatorCallTypes) {
			// validator tests - compare basic, extended, and executed calls

			for (int version = 1; version <= 2; version++) {

				TraceValidatorPredictionPattern.define(version);
				Logger.logPatternsReset(TraceValidatorPredictionPattern.patterns);

				for (String program : programs) {
					DatabaseInput.read(program);

					Seeder.seedInputMethodTraceValuesWithDeveloperGold();
					Seeder.seedInputClazzTraceValuesWithDeveloperGold();
					Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
					Definitions.callerType = Definitions.CallerType.basic;
					validate(program, "val-v" + version + "-" + Definitions.callerType.toString());

					Seeder.seedInputMethodTraceValuesWithDeveloperGold();
					Seeder.seedInputClazzTraceValuesWithDeveloperGold();
					Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
					Definitions.callerType = Definitions.CallerType.extended;
					validate(program, "val-v" + version + "-" + Definitions.callerType.toString());

					Seeder.seedInputMethodTraceValuesWithDeveloperGold();
					Seeder.seedInputClazzTraceValuesWithDeveloperGold();
					Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
					Definitions.callerType = Definitions.CallerType.executed;
					validate(program, "val-v" + version + "-" + Definitions.callerType.toString());
				}

				Logger.logPatterns("all", TraceValidatorPredictionPattern.patterns, "val-v" + version);
			}
		}

		if (runAllTests || test==Algorithm.ValidatorIterations) {
			// validator iterator tests
			TraceValidatorPredictionPattern.define(2);
			Logger.logPatternsReset(TraceValidatorPredictionPattern.patterns);

			for (String program : programs) {
				DatabaseInput.read(program);
				Seeder.seedInputMethodTraceValuesWithDeveloperGold();
				Seeder.seedInputClazzTraceValuesWithDeveloperGold();
				Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
				Definitions.callerType = Definitions.CallerType.extended;

				for (int iteration = 1; iteration <= 3; iteration++) {
					validate(program, "val-it" + iteration);
					Seeder.seedInputMethodRTMCellWithPredicted(true);
				}
			}
			Logger.logPatterns("all", TraceValidatorPredictionPattern.patterns, "val-it");
		}

		if (runAllTests || test==Algorithm.GhabiValidator) {
			// validator tests - ghabi
			TraceValidatorGhabiPredictionPattern.define();
			Logger.logPatternsReset(TraceValidatorGhabiPredictionPattern.patterns);
			Definitions.callerType = Definitions.CallerType.extended;

			for (String program : programs) {
				DatabaseInput.read(program);
				Seeder.seedInputMethodTraceValuesWithDeveloperGold();
				Seeder.seedInputClazzTraceValuesWithDeveloperGold();
				Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
				validateGhabi(program, "val-ghabi");
				Logger.logPatternsSeeding(program, TraceValidatorPredictionPattern.patterns, "Ghabi");
			}

			Logger.logPatterns("all", TraceValidatorGhabiPredictionPattern.patterns, "val-ghabi");
		}

		if (runAllTests || test==Algorithm.Refiner) {

			TraceRefinerPredictionPattern.define();
			TraceValidatorPredictionPattern.define(2);
			ArrayList patterns = new ArrayList();
			patterns.addAll(TraceRefinerPredictionPattern.patterns);
			patterns.addAll(TraceValidatorPredictionPattern.patterns);
			Logger.logPatternsReset(patterns);
			Definitions.callerType = Definitions.CallerType.extended;
			
//			for(Clazz clazz: Clazz.clazzesHashMap.values()) {
//				System.out.println(clazz.ID+"   "+clazz.getTcount());
//			}
			
			for (String program : programs) {

				DatabaseInput.read(program);
				Seeder.seedInputClazzTraceValuesWithDeveloperGold();
				Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
				refine(program, "ref", patterns);

			}
			Logger.logPatterns("all", patterns, "ref");
			
			 long endTime = System.currentTimeMillis();
		     System.out.println("It took " + (endTime - startTime) + " milliseconds");
		}
		
		if (runAllTests || test==Algorithm.VSM || test==Algorithm.LSI) {

			

			for (String program : programs) {
				DatabaseInput.read(program);
				Seeder.seedInputClazzTraceValuesWithDeveloperGold();
				Seeder.seedPredictedMethodTraceValuesWithUndefinedTraces();
				RTMCell.logTPTNFPFN2(program, "");

			}
			//test
			
			 long endTime = System.currentTimeMillis();
		     System.out.println("It took " + (endTime - startTime) + " milliseconds");
		}
		
		
	/////////////////////////////////////			
	dataAnalysisVariables(); 
	
	
	dataVariablesTraceDefinition(); 

	dataVariablesPrintforPythonInputFile(); 

	/////////////////////////////////////	
		
		
		
	}

	private static void dataVariablesTraceDefinition() {
		for(String programName: Variable.totalVariablesHashMap.keySet()) {
			LinkedHashMap<String, Variable> variablesPerProgram = Variable.totalVariablesHashMap.get(programName); 
			for(Variable variable: variablesPerProgram.values()) {
				for(Requirement req: Requirement.totalRequirementsHashMap.get(programName).values()) {
					List<Method> methods = variable.getMethodList(); 
					int countT=0; int countN=0; int countU=0; 
					for(Method method: methods) {
						LinkedHashMap<String, MethodRTMCell> methodRTMCellList = MethodRTMCell.Totalmethodtraces2HashMap.get(programName); 
						MethodRTMCell methodRTMCell = methodRTMCellList.get(req.ID+"-"+method.getID()); 
						TraceValue goldTraceValue = methodRTMCell.getGoldTraceValue(); 
						if(goldTraceValue.equals(TraceValue.Trace)) {
							countT++; 
						}
						else if (goldTraceValue.equals(TraceValue.NoTrace)) {
							countN++; 
						}
						else if (goldTraceValue.equals(TraceValue.UndefinedTrace)) {
							countU++; 
						}

					}
					
					int totalCount = countT+countN+countU; 
					double Tperc=(double)countT/totalCount*100; 
					double Nperc=(double)countN/totalCount*100; 
					double Uperc=(double)countU/totalCount*100; 
					
					if(totalCount!=0) {
						Tperc=round(Tperc,2); 
						Nperc=round(Nperc,2); 
						Uperc=round(Uperc,2); 
						//---------------------------------//
					
//						assignTNUToVariableMajorityAlg(Tperc, Nperc, Uperc, req, variable, programName); 
						assignTNUToVariableAtleast2Alg(Tperc, Nperc, Uperc, req, variable, programName);
//						assignTNUToVariableAllAlg(Tperc, Nperc, Uperc, req, variable, programName);

						
					}
					
				}
			}
		
		
		}
		
		
		
		///////////////////////////////////////////////////////////////////////////
		for(String programName: VariableRTMCell.totalVariableTracesHashMap.keySet()) {
			LinkedHashMap<String, Variable> variableTracesPerProgram = VariableRTMCell.totalVariableTracesHashMap.get(programName); 
			for(String key: variableTracesPerProgram.keySet()) {
//				System.out.println(variable.percT+"  "+variable.percN+"  "+variable.percU);
//				System.out.println(key+" :::::::::::::::"+variableTracesPerProgram.get(key).getTraceValue());

			}
		}
		System.out.println("over");
		
	}

	private static void assignTNUToVariableMajorityAlg(double percT, double percN, double percU, Requirement req, Variable variable, String ProgramName) {
		// TODO Auto-generated method stub
		
//		System.out.println(req.ID+"-"+variable.id);
		if(percT>percN && percT>percU)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.Trace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "T"); 
 
		}
		else if(percN>percT && percN>percU)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.NoTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "N"); 

		}
		else if(percU>percT && percU>percN)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 

		}
		else {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 
		}

	}
	
	
	
	private static void assignTNUToVariableAllAlg(double percT, double percN, double percU, Requirement req, Variable variable, String ProgramName) {
		// TODO Auto-generated method stub
		
//		System.out.println(req.ID+"-"+variable.id);
		if(percT>0 && percN==0 && percU==0)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.Trace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "T"); 
 
		}
		else if(percN>0 && percT==0 && percU==0)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.NoTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "N"); 

		}
		else if(percU>0 && percN==0 && percT==0)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 

		}
		else {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 
		}

	}
	
	private static void assignTNUToVariableAtleast2Alg(double percT, double percN, double percU, Requirement req, Variable variable, String ProgramName) {
		// TODO Auto-generated method stub
		
//		System.out.println(req.ID+"-"+variable.id);
		if(percT>2)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.Trace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "T"); 
 
		}
		else if(percN>2)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.NoTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "N"); 

		}
		else if(percU>2)  {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 

		}
		else {
			VariableRTMCell.totalVariableTracesHashMap.get(ProgramName).get(req.ID+"-"+variable.id).setTraceValue(TraceValue.UndefinedTrace);
			variableStorageTraces.get(ProgramName).put(req.ID+"-"+variable.id, "U"); 
		}

	}

	private static void dataVariablesPrintforPythonInputFile() throws IOException {
		// TODO Auto-generated method stub
		int i=0; 
	      FileWriter myWriter = new FileWriter("log//PythonInputDataVariables.txt");
			myWriter.write("gold,ProgramName,RequirementID,MethodID,VariableTrace\n");

		for (String programName : MethodRTMCell.Totalmethodtraces2HashMap.keySet()) {
			LinkedHashMap<String, MethodRTMCell> methodTraces = MethodRTMCell.Totalmethodtraces2HashMap.get(programName); 
		
			for(MethodRTMCell cell: methodTraces.values()) {
				
		if(!cell.getGoldTraceValue().equals(TraceValue.UndefinedTrace)) {
			    HashSet<Variable> vars = cell.getMethod().getMethodVars(); 
				int countT=0; int countN=0; int countU=0; 

			    for(Variable var: vars) {
					String traceValue=variableStorageTraces.get(programName).get(cell.getRequirement().ID+"-"+var.id); 
					if(traceValue!=null) {
						if(traceValue.equals("T")) {
							countT++; 
						}
						else if(traceValue.equals("N")) {
							countN++; 
						}
						else if(traceValue.equals("U")) {
							countU++; 
						}
						else countU++; 
					}
			    }
			
						
					

				
			
			int totalCount = countT+countN+countU; 
			double percT=(double)countT/totalCount*100; 
			double percN=(double)countN/totalCount*100; 
			double percU=(double)countU/totalCount*100; 
			
			if(totalCount!=0) {
				percT=round(percT,2); 
				percN=round(percN,2); 
				percU=round(percU,2); 
			}
			
			
			//ALG 1:
//			String val=AlgMajority(percT, percN, percU); 
			//ALG 2:
			String val=AlgGreaterThan2(percT, percN, percU); 
			//ALG 3:
//			String val=AlgAll(percT, percN, percU); 

			
			
			
//			if(!cell.getGoldTraceValue().equals(TraceValue.UndefinedTrace)) {
				
					myWriter.write(cell.getGoldTraceValue()+","+programName+","+cell.getRequirement().ID+","+cell.getMethodID()+","+val+"\n");
								
					
				i++; 
//			}
			}
		}
			
		}
		myWriter.close();}

	private static String AlgAll(double percT, double percN, double percU) {
		// TODO Auto-generated method stub
		String val=""; 
		if(percT>0 && percN==0 && percU==0) val="T"; 
		else if (percN>0 && percT==0 && percU==0) val="N"; 
		else if(percU>0 && percT==0 && percN==0) val="U"; 
		
		return val;
	}

	private static String AlgGreaterThan2(double percT, double percN, double percU) {
		// TODO Auto-generated method stub
		String val=""; 
		if(percT>2) val="T";
		else if(percN> 2 )val="N";
		else if(percU>2) val="U";
		return val;
	}

	private static String AlgMajority(double percT, double percN, double percU) {
		// TODO Auto-generated method stub
		String val=""; 
		if(percT>percN && percT>percU) val="T";
		else if(percN> percT && percN>percU)val="N";
		else if(percU> percT && percU>percN) val="U";
		return val;
	}

	private static void dataAnalysisVariables() {
		HashMap<Integer, Integer> countHashmap = new HashMap<>(); 
		HashMap<Integer, Integer> MethVarsHashmap = new HashMap<>(); 
		System.out.println("METHODS USING CLASS FIELDS");
		System.out.println("HOW MANY METHODS USING 0, 1, 2, ... CLASS FIELDS WITHOUT DUPLICATES");

		for(String programName: Method.totalMethodsHashMap.keySet()) {
			LinkedHashMap<String, Method> methodhashmap = Method.totalMethodsHashMap.get(programName); 
			for( Method method: methodhashmap.values()) {
				HashSet<Variable> vars = method.getMethodVars(); 	
				 VariableList params = method.getParameters(); 	
				 vars.addAll(params); 
				HashSet<Object> seen=new HashSet<>();
				vars.removeIf(e->!seen.add(e.getId()));
				
				int numberOfVars=seen.size(); 
				
				if(MethVarsHashmap.get(numberOfVars)==null) {
					MethVarsHashmap.put(numberOfVars, 1); 
				}
				else {
					int numberOfMethods=MethVarsHashmap.get(numberOfVars)+1; 
					MethVarsHashmap.put(numberOfVars, numberOfMethods); 
				}
				
				
			}
			
		}
		
		
		
		
		
		
		for (Integer numberOfVars: MethVarsHashmap.keySet()){
			Integer numberOfMeth=MethVarsHashmap.get(numberOfVars); 
            System.out.println(numberOfVars + " " + numberOfMeth);  
	} 
		
		
		System.out.println("---------------------");
		System.out.println("HOW MANY METHODS USING 0, 1, 2, ... CLASS FIELDS");
		for(String programName: Method.totalMethodsHashMap.keySet()) {
		LinkedHashMap<String, Method> methodhashmap = Method.totalMethodsHashMap.get(programName); 
		for( Method method: methodhashmap.values()) {
			int size= method.getFieldMethods().size(); 
			
			if(countHashmap.get(size)==null) {
				countHashmap.put(size,1); 
			}
			else if(countHashmap.get(size)!=null) {
				int amount=countHashmap.get(size); 
				amount++; 
				countHashmap.put(size, amount); 
			}
			
		}
		
	}
		for (Integer variablesSize: countHashmap.keySet()){
			Integer NumberOfMethods=countHashmap.get(variablesSize); 
            System.out.println(variablesSize + " " + NumberOfMethods);  
	} 
		
		HashMap<Integer, Integer> countHashMapNew= new HashMap<>(); 
		for(Clazz myclass: Clazz.clazzesHashMap.values()) {
			List<Variable> fields = myclass.getFieldClasses(); 
			for(Variable field: fields) {
				List<Method> methods = field.getMethodList(); 
				if(countHashMapNew.get(methods.size())==null)
					{
					countHashMapNew.put(methods.size(), 1); 
					}
				else {
					int count=countHashMapNew.get(methods.size()); 
					count++; 
					countHashMapNew.put(methods.size(), count); 
				}
				
				if(methods.size()==0) {
					System.out.println(field);
					System.out.println("here");
				}
			}
		}
		
		
		for(int methodsSize: countHashMapNew.keySet()) {
			System.out.println(methodsSize+"-"+countHashMapNew.get(methodsSize));
			
		}
		/****************************/
		for(  String programName: Variable.totalVariablesHashMap.keySet()) {
			int empty=0;
			LinkedHashMap<String, Variable> varHashMap = Variable.totalVariablesHashMap.get(programName); 	
			for(  Variable var: varHashMap.values()) {
				for(   Requirement req: Requirement.totalRequirementsHashMap.get(programName).values()) {
					int countT=0; int countN=0; int countU=0; 

					if(!var.getMethodList().isEmpty()) {
						for(  Method meth: var.getMethodList()) {

							TraceValue gold =MethodRTMCell.Totalmethodtraces2HashMap.get(programName).get(req.ID+"-"+meth.ID).getGoldTraceValue(); 
							
							
						
					
							if(gold.equals(TraceValue.Trace)) countT++; 
							else if(gold.equals(TraceValue.NoTrace)) countN++; 
							else if(gold.equals(TraceValue.UndefinedTrace)) countU++; 
						}
						
						int totalCount = countT+countN+countU; 
						double Tperc=(double)countT/totalCount*100; 
						double Nperc=(double)countN/totalCount*100; 
						double Uperc=(double)countU/totalCount*100; 
						
						if(totalCount!=0) {
							Tperc=round(Tperc,2); 
							Nperc=round(Nperc,2); 
							Uperc=round(Uperc,2); 
						}
						

						
						if(totalCount!=0) {
//							System.out.println(programName+","+req.ID+","+var.variableName+","+var.ownerclazz.ID+","+var.ownerclazz.name+","+countT+","+countN+","+countU+","+totalCount+","+Tperc+","+Nperc+","+Uperc);
						}
					
					}
					else {
						empty++; 
					}
			
				}
				
			}
			
//			System.out.println("=====>EMPTY  "+programName+ "  "+empty);
		}
	System.out.println();
//	System.out.println("OVER");
	}
	
	
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	static public void validate(String programName, String logParameter) throws Exception {
		PredictionPattern.reset();
		TraceValidator.makePredictions();

		Logger.logBasics(programName, logParameter);
		Logger.logDetailed(programName, logParameter);
		Logger.logPatternsEntry(programName, logParameter, TraceValidatorPredictionPattern.patterns);
	}
	
	static public void validateGhabi(String programName, String logParameter) throws Exception {
		PredictionPattern.reset();

		TraceValidatorGhabi.makePredictions();

		Logger.logBasics(programName, logParameter);
		Logger.logDetailed(programName, logParameter);
		Logger.logPatternsEntry(programName, logParameter, TraceValidatorGhabiPredictionPattern.patterns);
	}


	static public void refine(String programName, String logParameter, ArrayList patterns) throws Exception {
		PredictionPattern.reset();

		Seeder.seedInputClazzTraceValuesWithDeveloperGold();

		TraceRefiner.step1_classNs2MethodNs();
		RTMCell.logTPTNFPFN2(programName, "step 1");
		
		TraceRefiner.step2_propagateMethodNs(1);
		RTMCell.logTPTNFPFN2(programName, "step 2");

		TraceRefiner.step3_classTs2MethodTs();
		RTMCell.logTPTNFPFN2(programName, "step 3");

		TraceRefiner.step4_propagateMethodTs(1);
		RTMCell.logTPTNFPFN2(programName, "step 4");

		TraceRefiner.checkGoldPred(programName); 
		
		Logger.logBasics(programName, logParameter);
		Logger.logDetailed(programName, logParameter);
		Logger.logPatternsEntry(programName, logParameter, patterns);	
		
//		System.out.println("Type, reqMethod, CalleesSize, CallersSize, CallersCallersSize");
//		for (MethodRTMCell methodtrace : MethodRTMCell.methodtraces2HashMap.values()) {
//			String reqMethod= methodtrace.getRequirement().ID+"_"+methodtrace.getMethod().ID; 
//			if(methodtrace.logTPFPTNFN(programName).contains("FP_T")) {
//				//inner 
//				if (!methodtrace.getCallees().isEmpty() && !methodtrace.getCallers().isEmpty()) {
//				 System.out.println("Inner,"+reqMethod+","+methodtrace.getCallees().size()+","+methodtrace.getCallers().size()+",,");
//				}
//				//leaf 
//				else if (methodtrace.getCallees().isEmpty() && !methodtrace.getCallers().isEmpty()) {
//					 System.out.println("Leaf,"+reqMethod+",,"+methodtrace.getCallers().size()+","+methodtrace.getCallers().getCallers().size());
//
//				}
//			}
//			System.out.println("*******************************************************");
//			if(methodtrace.logTPFPTNFN(programName).contains("TP_T")) {
//				//inner 
//				if (!methodtrace.getCallees().isEmpty() && !methodtrace.getCallers().isEmpty()) {
//				 System.out.println(methodtrace.getCallees().size()+"   "+methodtrace.getCallers().size());
//				}
//				//inner 
//				else if (methodtrace.getCallees().isEmpty() && !methodtrace.getCallers().isEmpty()) {
//					 System.out.println(methodtrace.getCallees().size()+"   "+methodtrace.getCallers().size());
//
//				}
//			}
//			
//		}
	
	
	
	
	}



}

