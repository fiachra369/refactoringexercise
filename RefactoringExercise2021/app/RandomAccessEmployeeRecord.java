/*
 * 
 * This is a Random Access Employee record definition
 * 
 * */

import java.io.RandomAccessFile;
import java.io.IOException;

public class RandomAccessEmployeeRecord extends Employee
{  
    public static final int SIZE = 175; // Size of each RandomAccessEmployeeRecord object

   // Create empty record
   public RandomAccessEmployeeRecord()
   {
      this(0, "","","",'\0', "", 0.0, false);
   } 

   // Initialize record with details
   public RandomAccessEmployeeRecord( int employeeId, String pps, String surname,
		   String firstName, char gender, String department, double salary, boolean fullTime)
   {
      super(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
   } 

   // Read a record from specified RandomAccessFile
   public void read( RandomAccessFile file ) throws IOException
   {
	   	extractedReading(file);
   }

   // Ensure that string is correct length
   private String readName( RandomAccessFile file ) throws IOException
   {
      char name[] = new char[ 20 ], temp;

      for ( int count = 0; count < name.length; count++ )
      {
         temp = file.readChar();
         name[ count ] = temp;
      }     
      return new String( name ).replace( '\0', ' ' );
   } 

   // Write a record to specified RandomAccessFile
   public void write( RandomAccessFile file ) throws IOException
   {
      extractedWriteRecord(file);
   } 

   // Ensure that string is correct length
   private void writeName( RandomAccessFile file, String name )
      throws IOException
   {
      StringBuffer buffer = null;

      if ( name != null ) 
         buffer = new StringBuffer( name );
      else 
         buffer = new StringBuffer( 20 );

      buffer.setLength( 20 );
      file.writeChars( buffer.toString() );
   }
   
   //*************EXTRACTED METHODS********************
   private void extractedReading(RandomAccessFile file) throws IOException {
		setEmployeeId(file.readInt());
		setPps(readName(file));
		setSurName(readName(file));
		setFirstName(readName(file));
		setGender(file.readChar());
		setDepartment(readName(file));
		setSalary(file.readDouble());
		setFullTime(file.readBoolean());
	}
   
   private void extractedWriteRecord(RandomAccessFile file) throws IOException {
		file.writeInt( getEmployeeId() );
	      writeName(file, getPps().toUpperCase());
	      writeName( file, getSurName().toUpperCase() );
	      writeName( file, getFirstName().toUpperCase() );
	      file.writeChar(getGender());
	      writeName(file,getDepartment());
	      file.writeDouble( getSalary() );
	      file.writeBoolean(getFullTime());
	}
   //**********END EXTRACT METHODS************************
} // end class RandomAccessEmployeeRecord