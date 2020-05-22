package thirdmilestone;

import java.io.BufferedWriter;
import java.io.IOException;

public class WriteAttibutes {
	public static void writeAttributes(BufferedWriter buffWriter) throws IOException {
		buffWriter.write("@ATTRIBUTE versionname REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #revision REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE #fixcommit REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE size REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE churn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchurn REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE chgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE maxchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE avgchgsetsize REAL");
	     buffWriter.newLine();
	     buffWriter.write("@ATTRIBUTE buggy {no,yes}");
	     buffWriter.newLine();
	     buffWriter.write("@DATA");
	     buffWriter.newLine();
	}
}
