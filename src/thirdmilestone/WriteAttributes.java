package thirdmilestone;

import java.io.BufferedWriter;
import java.io.IOException;

public class WriteAttributes {
	private  WriteAttributes() {
		
	}
	public static void writeAttributes(BufferedWriter buffWriter) throws IOException {
		String[] attributeList = {"REAL", "#revision", "#fixcommit","size", "churn", "maxchurn", "avgchurn","chgsetsize","maxchgsetsize","avgchgsetsize"};
		for(int i = 0; i< attributeList.length; i++) {
			buffWriter.write("@ATTRIBUTE "+ attributeList[i] + " REAL");
			buffWriter.newLine();
		}
		
		buffWriter.write("@ATTRIBUTE buggy {no,yes}");
	     buffWriter.newLine();
	     buffWriter.newLine();
	     buffWriter.write("@DATA");
	     buffWriter.newLine();
	}
}
