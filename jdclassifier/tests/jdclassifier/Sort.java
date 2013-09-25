package jdclassifier;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.decker.jdclassifier.JDClassifier;
import com.decker.jdclassifier.JDClassifierException;
import com.decker.jdclassifier.JDDocument;
import com.decker.jdclassifier.Result;
import com.decker.jdclassifier.StringTokenizer;
import com.decker.jdclassifier.JDClassifier.JDParameter;

public class Sort {

	public static void main(String[] args) throws JDClassifierException, IOException, InterruptedException {
		Map<JDParameter,String> parameters =  new HashMap<JDParameter,String>();
		parameters.put(JDParameter.DATABASE_NAME, "test");

		JDClassifier classifier = new JDClassifier(parameters);

		File[] files = new File(System.getProperty( "user.home" ),"/TestData/Input").listFiles();
		for(File file : files)
		{
			System.out.println(String.format("File: %s", file.getName()));
			String data = readFile(file.getAbsolutePath());
			Result[] results = classifier.classify(new StringTokenizer(new JDDocument("something","something"),data.split("\r?\n")));
			for(Result result :results)
				System.out.println(String.format("Name: %s Matches: %s Score: %s Adjusted: %s", result.name,result.matches,result.scoring,result.scoring/result.matches));
			if(results.length > 0)
			{
				File outputFile = new File(String.format("%s/TestData/Classified/%s", System.getProperty( "user.home" ),results[0].name));
				outputFile.mkdirs();
				outputFile = new File(outputFile,file.getName());
				file.renameTo(outputFile);
			}
			
			System.out.println();
		}

		classifier.close();

	}

	static String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
