package exercise2;				// One collection with nested documents

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Person;

public class Exercise_2_model_2 {

	public static void dataGenerator(int N) {
			Fairy fairy = Fairy.create();
			
			MongoClient client = new MongoClient();
			MongoDatabase database = client.getDatabase("test");

			// create collection model_2
			MongoCollection<Document> model_2 = database.getCollection("model_2");
			model_2.deleteMany(new Document());
		
			String oneName = "";
			for (int i = 0; i < N; ++i) {
				// company name as _id
				Company company = fairy.company();
				
				Document randomcompany = new Document();

				randomcompany.put("domain", company.domain());
				randomcompany.put("email", company.email());
				String companyname=company.name();
				randomcompany.put("name", companyname);
				randomcompany.put("url", company.url());
				randomcompany.put("vatIdentificationNumber", company.vatIdentificationNumber().toString());
				
				
				int nemployees;
				Random rn = new Random();
				nemployees = rn.nextInt(100)+1;	// avoid 0 workers company
				List<DBObject> workers = new ArrayList<DBObject>();
				
				for (int j = 0; j < nemployees; ++j) {
					Person person = fairy.person();
					
					DBObject randomperson = new BasicDBObject();
					
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
					
					workers.add(randomperson);
					System.out.println("Insert person: " + j + " company: " + i + " "+ companyname);
					
					
				}
				
				randomcompany.put("employees", workers);
				model_2.insertOne(randomcompany);
				
				// System.out.println("Insert company " + i);
				
			}

			
	// Q1 - For each person, its name and its company name
						
			System.out.println("Q1 ======================================================================================================================================");
			
			// get company names
			BasicDBObject query = new BasicDBObject();
			FindIterable<Document> companies = model_2.find();
					// db.model_2.find({},{name:1})
				
			// iterate companies
			for (Document c:companies)
				{
					//System.out.println(c.get("name"));
					String cname = c.get("name").toString();
					ArrayList<DBObject> workers=(ArrayList<DBObject>) c.get("employees");
					System.out.println(cname + ": ----------------------------------------------------------------------------------------");
					
					// iterate people in company, "employees"
					for( Iterator< DBObject > it = workers.iterator(); it.hasNext(); )	
				     	{
							// DBObject worker=iterator.next().get("fullName);
							Document doc;
							doc=(Document) it.next();
							// print results
							System.out.println(doc.get("fullName")+" works at " + cname);
							
						}					
				}

			
			
			
	// Q2- For each company, the name and the number of employees
		
			System.out.println("");
			System.out.println("Q1 ======================================================================================================================================");

			// get company names
			companies = model_2.find();
				
			// iterate companies
			for (Document c:companies)
				{
					String cname = c.get("name").toString();
					ArrayList<DBObject> workers=(ArrayList<DBObject>) c.get("employees");
					
					// iterate "employees" and count
					int count = 0;
					for( Iterator< DBObject > it = workers.iterator(); it.hasNext(); )	
				     	{
							// count
						    it.next();
							count++;
						}	
					System.out.println(cname + " has " + count + " employees");
				}
			
		    
			
	// Q3 - Average age per company
		    
			System.out.println("");
			System.out.println("Q3 ======================================================================================================================================");
			
			// get company names
			companies = model_2.find();
				
			// iterate companies
			for (Document c:companies)
				{
					String cname = c.get("name").toString();
					ArrayList<DBObject> workers=(ArrayList<DBObject>) c.get("employees");
					
					// iterate people in company, "employees"
					double totalp = 0;
					double totalage = 0;
					Object age;
					DecimalFormat df = new DecimalFormat("#.#####");
					
					for( Iterator< DBObject > it = workers.iterator(); it.hasNext(); )	
				     	{
							int a=0;
							Document doc;
							doc=(Document) it.next();
							age=doc.get("age");
							a=Integer.parseInt(age.toString());
							totalage=totalage+a;
							totalp++;
						}
					System.out.println(cname + " average age is " + df.format(totalage/totalp));
				}

		
	}
}