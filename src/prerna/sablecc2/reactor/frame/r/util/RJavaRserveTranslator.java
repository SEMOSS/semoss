package prerna.sablecc2.reactor.frame.r.util;

import java.io.IOException;
import java.util.Map;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import prerna.engine.impl.r.RSingleton;
import prerna.sablecc2.om.NounMetadata;
import prerna.sablecc2.om.PixelDataType;
import prerna.sablecc2.reactor.runtime.AbstractBaseRClass;

public class RJavaRserveTranslator extends AbstractRJavaTranslator {

	protected RConnection retCon;
	protected String port;

	/**
	 * Have this be protected since we want the control to be based on the 
	 * RJavaTranslator to determine which class to generate
	 */
	protected RJavaRserveTranslator() {

	}

	/**
	 * This will start R, only if it has not already been started
	 * In this case we are establishing a connection for Rserve
	 */
	@Override
	public void startR() {
		// we store the connection in the PKQL Runner
		// retrieve it if it is already defined within the insight
		NounMetadata noun = (NounMetadata) this.insight.getVarStore().get(R_CONN);
		if (noun != null) {
			retCon = (RConnection) this.insight.getVarStore().get(R_CONN).getValue();
		}
		NounMetadata nounPort = this.insight.getVarStore().get(R_PORT);
		if (nounPort != null) {
			port = (String) nounPort.getValue();
		}
		logger.info("Connection right now is set to.. " + retCon);
		if (retCon == null) {
			try {
				this.retCon = RSingleton.getConnection();
//				port = Utility.findOpenPort();
//				logger.info("Starting it on port.. " + port);
//				// need to find a way to get a common name
//				masterCon.eval("library(Rserve); Rserve(port = " + port + ")");
//				retCon = new RConnection("127.0.0.1", Integer.parseInt(port));
				
				// load all the libraries
				retCon.eval("library(splitstackshape);");
				logger.info("Loaded packages splitstackshape");
				// data table
				retCon.eval("library(data.table);");
				logger.info("Loaded packages data.table");
				// reshape2
				retCon.eval("library(reshape2);");
				logger.info("Loaded packages reshape2");
				// rjdbc
				retCon.eval("library(RJDBC);");
				logger.info("Loaded packages RJDBC");
				// stringr
				retCon.eval("library(stringr)");
				logger.info("Loaded packages stringr");

				this.insight.getVarStore().put(AbstractBaseRClass.R_CONN, new NounMetadata(retCon, PixelDataType.R_CONNECTION));
				this.insight.getVarStore().put(AbstractBaseRClass.R_PORT, new NounMetadata(port, PixelDataType.CONST_STRING));
			} catch (Exception e) {
				System.out.println(
						"ERROR ::: Could not find connection.\nPlease make sure RServe is running and the following libraries are installed:\n"
								+ "1)Rserve\n" + "2)splitstackshape\n" + "3)data.table\n" + "4)reshape2\n"
								+ "5)RJDBC*\n" + "6)stringr\n\n"
								+ "*Please note RJDBC might require JAVA_HOME environment path to be defined on your system.");
				e.printStackTrace();
				throw new IllegalArgumentException(
						"ERROR ::: Could not find connection.\nPlease make sure RServe is running and the following libraries are installed:\n"
								+ "1)Rserve\n" + "2)splitstackshape\n" + "3)data.table\n" + "4)reshape2\n"
								+ "5)RJDBC*\n" + "6)stringr\n\n"
								+ "*Please note RJDBC might require JAVA_HOME environment path to be defined on your system.");
			}
		}
	}

	@Override
	public Object executeR(String rScript) {
		try {
			return retCon.eval(rScript);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getString(String script) {
		try {
			return retCon.eval(script).asString();
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getStringArray(String script) {
		try {
			return retCon.eval(script).asStrings();
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getInt(String script) {
		int number = 0;
		try {
			number = retCon.eval(script).asInteger();
			return number;
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return number;
	}

	@Override
	public int[] getIntArray(String script) {
		try {
			return retCon.eval(script).asIntegers();
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public double getDouble(String script) {
		double number = 0;
		try {
			number = retCon.eval(script).asDouble();
			return number;
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return number;
	}

	@Override
	public double[] getDoubleArray(String script) {
		try {
			return retCon.eval(script).asDoubles();
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getFactor(String script) {
		try {
			return retCon.eval(script).asFactor();
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public double[] getHistogramBreaks(String script) {
		try {
			REXP x = retCon.eval(script);
			Map<String, Object> histJ = (Map<String, Object>) (retCon.eval(script).asNativeJavaObject());
			double[] breaks = (double[]) histJ.get("breaks");
			return breaks;
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int[] getHistogramCounts(String script) {
		try {
			Map<String, Object> histJ = (Map<String, Object>) (retCon.eval(script).asNativeJavaObject());
			int[] counts = (int[]) histJ.get("counts");
			return counts;
		} catch (RserveException e) {
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object parseAndEvalScript(String script) throws IOException {
		try {
			return retCon.parseAndEval(script);
		} catch (REngineException e) {
			throw new IOException("Error with execution of : " + script);
		} catch (REXPMismatchException e) {
			throw new IOException("Error with execution of : " + script);
		}
	}

	@Override
	public void setConnection(RConnection connection) {
		if (connection != null) {
			this.retCon = connection;
		}
	}

	@Override
	public void setPort(String port) {
		if (this.port != null) {
			this.port = port;
		}
	}

	@Override
	public void endR() {
		// only have 1 connection
		// do not do this..
//		try {
//			if(retCon != null) {
//				retCon.shutdown();
//			}
//			// clean up other things
//			System.out.println("R Shutdown!!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void runR(String script) {
		runR(script, true);
	}

	public void runR(String script, boolean result) {
		String newScript = "paste(capture.output(print(" + script + ")),collapse='\n')";
		Object output;
		try {
			output = this.parseAndEvalScript(newScript);
			if (result && output != null) {
				try {
					System.out.println(((REXP) output).asString());
				} catch (REXPMismatchException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			output = this.executeR(script);
			if (result) {
				System.out.println("RCon data.. " + output);
				StringBuilder builder = new StringBuilder();
				getResultAsString(output, builder);
				System.out.println("Output : " + builder.toString());
			}
		}
	}
}
