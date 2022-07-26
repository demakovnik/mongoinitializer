package org.demakov.mongoinitializer;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.demakov.mongoinitializer.generator.JavaDataClassGenerator;
import org.demakov.mongoinitializer.generator.YamlClassCompilator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App 
{

    private static final String URL = "mongodb://localhost:27017";
    private JavaDataClassGenerator javaDataClassGenerator;


    public App() throws IOException {
        this.javaDataClassGenerator = new JavaDataClassGenerator();
    }



    public static void main(String[] args) {
        List<Class> classes = new ArrayList<>();
        try {
            App app = new App();
            File yamlFile = app.getFileFromResource("yaml/yamlFile.yaml");
            app.getJavaDataClassGenerator().generateJavaSourceFiles(yamlFile);
            classes = YamlClassCompilator.compile(yamlFile.getParentFile());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Replace the uri string with your MongoDB deployment's connection string
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        try (MongoClient mongoClient = MongoClients.create(URL)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Document> collection = database.getCollection("movies");
            Object o = classes.get(0).newInstance();




            try {
                InsertOneResult result = collection.insertOne(new Document()
                        .append("_id", new ObjectId())
                        .append("object", o));
                System.out.println("Success! Inserted document id: " + result.getInsertedId());
            } catch (MongoException me) {
                System.err.println("Unable to insert due to an error: " + me);
            }

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }

    public JavaDataClassGenerator getJavaDataClassGenerator() {
        return javaDataClassGenerator;
    }

    public void setJavaDataClassGenerator(JavaDataClassGenerator javaDataClassGenerator) {
        this.javaDataClassGenerator = javaDataClassGenerator;
    }


}

