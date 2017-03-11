package exercise2;		/* Two collections, one for each class and reference fields */

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Person;
import com.mongodb.client.model.Aggregates.*;

public class Exercise_2_model_1 {

	public static void dataGenerator(int N) {
		Fairy fairy = Fairy.create();
		
		MongoClient client = new MongoClient();
		MongoDatabase database = client.getDatabase("test");
		
		// create employee collection
		MongoCollection<Document> employees = database.getCollection("employees");
		employees.deleteMany(new Document());
		
		// create companies collection
		MongoCollection<Document> companies = database.getCollection("companies");
		companies.deleteMany(new Document());

		
		String oneName = "";
		for (int i = 0; i < N; ++i) {
			Company company = fairy.company();
			
			//if (i == 0) oneName = person.firstName();
			
			
			Document randomcompany = new Document();

			
			randomcompany.put("domain", company.domain());
			randomcompany.put("email", company.email());
			String companyname=company.name();
			randomcompany.put("name", companyname);
			randomcompany.put("url", company.url());
			randomcompany.put("vatIdentificationNumber", company.vatIdentificationNumber().toString());
			
			companies.insertOne(randomcompany);
			int nemployees;
			Random rn = new Random();
			nemployees = rn.nextInt(N)+1;        // avoid 0 workers company
			
			// for each company add nemployees
			for (int j = 0; j < nemployees; ++j) {
				Person person = fairy.person();

				Document randomperson = new Document();
				
				String fullName=person.fullName();
				
				// set 1 boss per company, first employee
				if (j==0) randomperson.put("boss", fullName);
				
				Random r = new Random();

				randomperson.put("age", 16+r.nextInt(48));
				
				randomperson.put("companyEmail", person.companyEmail());
				randomperson.put("dateOfBirth", person.dateOfBirth().toString());
				randomperson.put("email", person.email());
				randomperson.put("firstName", person.firstName());
				randomperson.put("fullName", fullName);
				randomperson.put("sex", person.sex().toString());
				
				// company name is added for each employee, simplifying Q2 and Q3
				randomperson.put("company",companyname);
				
				employees.insertOne(randomperson);
				System.out.println("Insert person: " + j + " company: " + i + " "+ companyname);
				
				
			}
				
			
			// System.out.println("Insert company " + i);
			
		}
		
		
// Q1 - For each person, its name and its company name
		
	// MONGO DB shell: db.employees.find({},{fullName:1, _id:0, company:1})	
		
		// create an empty query
		BasicDBObject query = new BasicDBObject();
		
		// execute query, projection with "fullName", "company", "boss"
		FindIterable<Document> workers = employees.find(query).projection(Projections.include("fullName", "company", "boss"));
        
		for( Document p: workers){
			String worker=p.getString("fullName");
			String comp=p.get("company").toString();
			String boss;
			try
				{
					boss=p.get("boss").toString();
					boss=" and is the boss";
				}	
			catch (Exception name)
			{
				boss="";
			}

			System.out.println(worker + " works at " + comp + boss);
		}
			
// Q2- For each company, the name and the number of employees
		
	// MONGO DB shell: db.employees.aggregate( [ { $group: {"_id": "$company", "total": {$sum: 1 }}} ] )
		
	   FindIterable<Document> comp = companies.find();
		
	   for (Document c:comp) 
	     {
		   BasicDBObject query2 = new BasicDBObject("company", c.get("name"));

		   FindIterable<Document> people = employees.find(query2);
		   int totalworkers=0;
		   for (Document p:people)
		   		{
			       totalworkers++;		   		   
		   		}
		   
	       System.out.println(c.get("name") + " has " + totalworkers + " employees");
		   
	     }

		
// Q3 - Average age per company
	    
	   for (Document c:comp) 
	     {
		   BasicDBObject query2 = new BasicDBObject("company", c.get("name"));

		   DecimalFormat df = new DecimalFormat("#.#####");
		   FindIterable<Document> people = employees.find(query2);
		   double totalworkers=0;
		   double totalage=0;
			for( Iterator<Document> it = people.iterator(); it.hasNext(); )	
	     	{
				int a=0;
				Document doc;
				doc=(Document) it.next();
				Object age = doc.get("age");
				a=Integer.parseInt(age.toString());
				totalage=totalage+a;
				totalworkers++;
			}
		   
			System.out.println(c.get("name") + " average age is " + df.format(totalage/totalworkers));		   
	     }
		client.close();	
	}

}