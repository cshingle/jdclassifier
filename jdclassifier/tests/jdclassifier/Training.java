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
import com.decker.jdclassifier.JDClassifier.JDParameter;
import com.decker.jdclassifier.JDClassifierException;
import com.decker.jdclassifier.JDDocument;
import com.decker.jdclassifier.StringTokenizer;

public class Training {

	public static void main(String[] args) throws JDClassifierException, IOException 
	{
		Map<JDParameter,String> parameters =  new HashMap<JDParameter,String>();
		parameters.put(JDParameter.DATABASE_NAME, "test");

		JDClassifier classifier = new JDClassifier(parameters);

		File[] files = new File(System.getProperty( "user.home" ),"/TestData/TrainingData").listFiles();
		for(File file : files)
		{
			String type = file.getName().replaceAll("_.*$", "");
			JDDocument document = classifier.createDocument(type, type);
			String data = readFile(file.getAbsolutePath());
			classifier.train(new StringTokenizer(document,data.split("\r?\n")));
		}

		classifier.close();
	}

	static String readFile(String path) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
