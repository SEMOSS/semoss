package prerna.engine.impl.r;

import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.SystemUtils;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import prerna.util.DIHelper;

public class RSingleton {
	
	private static RConnection rcon = null;
	static int port = 6311;
	public static final String R_HOME="R_HOME";
	static Hashtable <Integer, RConnection> portToCon = new Hashtable<Integer, RConnection>(); // RServe is not allowing me to inspect the port so I have to do this.. sorry
	
	private RSingleton() {
		
	}
	
	public static void startRServe(int port)
	{
		try {
			String rHome = System.getenv("R_HOME");
			System.out.println("RHome is ... " + rHome);
			
			// If R_HOME doesn't exist, then check RDF_Map
			if(rHome == null || rHome.isEmpty()) {
				rHome = DIHelper.getInstance().getProperty(R_HOME);
			}
			
			rHome = rHome.replace("\\", System.getProperty("file.separator"));

			rHome = rHome + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "R";

			if(SystemUtils.IS_OS_WINDOWS)
				rHome = rHome.replace(System.getProperty("file.separator"), "\\\\");

			System.out.println("RHome is ... " + rHome);
			
			ProcessBuilder pb = new ProcessBuilder("" + rHome + "", "-e", "library(Rserve);Rserve(FALSE," + port + ",args='--vanilla');flush.console <- function(...) {return;};options(error=function() NULL)", "--vanilla");
			Process process = pb.start();
			
			System.out.println("Waiting 1 second to allow Rserve to finish starting up...");
			try {
				process.waitFor(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//ProcessBuilder pb = new ProcessBuilder("\"" + rHome + "\"", "-e", "\"library(Rserve);Rserve(FALSE,args='--vanilla --RS-port" + port + "');flush.console <- function(...) {return;}; options(error=function() NULL)\"", "--vanilla");

			//Process p = pb.start();
			//p.destroy();
			System.out.println("Started.. process builder.. !!");
			//Runtime.getRuntime().exec(" \"" + rHome + "\" -e \"library(Rserve);Rserve(FALSE,args='--vanilla --RS-port" + port + "');flush.console <- function(...) {return;}; options(error=function() NULL)\" --vanilla");
			//System.out.println("R Started.. going to end");
			//Runtime.getRuntime().exec(" \"C:\\Program Files\\R\\R-3.3.0\\bin\\R.exe\" -e \"library(Rserve); library(RSclient); rsc <- RSconnect(port = 6311); RSshutdown(rsc)\" --vanilla");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void stopRServe()
	{
		stopRServe(port);
	}
	
	public static void stopRServe(int port)
	{
		try {
			String rHome = System.getenv("R_HOME");
			
			rHome = rHome.replace("\\", System.getProperty("file.separator"));

			rHome = rHome + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + "R";

			if(SystemUtils.IS_OS_WINDOWS)
				rHome = rHome.replace(System.getProperty("file.separator"), "\\\\");

			System.out.println("RHome is ... " + rHome);

//			Runtime.getRuntime().exec(" \"" + rHome+ "\" -e \"library(Rserve);library(RSclient); rsc<-RSconnect(port=" + port + ");RSshutdown(rsc) --vanilla");
			
			ProcessBuilder pb = new ProcessBuilder("" + rHome + "", "-e", "library(Rserve);library(RSclient);rsc<-RSconnect(port=" + port + ");RSshutdown(rsc)", "--vanilla");
			Process process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	public static RConnection getConnection() {
		
		
		if(rcon == null) {
			return getConnection("127.0.0.1", 6311);
		}
		return rcon;
	}
	
	public static RConnection getConnection(int port) {
		return getConnection("127.0.0.1", port);		
	}
	
	public static RConnection getConnection(String host, int port) {
		
		// this basically tries to do the same as get connection with a port
		
		
		if(portToCon.containsKey(port)) {
			rcon = portToCon.get(port);
		} else {
			System.out.println("Making a master connection now on port" + port);;
			try {
				
				rcon = new RConnection(host, port);
				portToCon.put(port, rcon);
				
			}catch(Exception ex)
			{
				ex.printStackTrace();
				// try to start again and see if that works
				startRServe(port);
				try {
					rcon = new RConnection(host, port);
					portToCon.put(port, rcon);
					RSingleton.port = port;
				} catch (RserveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		return rcon;
	}
	

}
