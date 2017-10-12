
package assignment;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class LZ77
{
	public static void main (String[] args){
            Scanner sc = new Scanner(System.in);
            String word =sc.nextLine();
            ArrayList<Tag> Tags; // array of Tags
            Tags = compression(word);
            System.out.println(decompression (Tags));
            set_tags_in_file(Tags);
            Tags = get_tags_from_file();
            System.out.println(decompression (Tags)); 
//            set_string_in_file(word);
//            word = get_string_from_file();
//            Tags = compression(word);
//            System.out.println(word);
	}
//        public static String get_string_from_file(){
//            String word = "";
//            try (FileReader file = new FileReader("word.txt")) {
//                int x;
//                while((x = file.read()) != -1){
//                    word += (char)x;
//                }
//            }
//            catch(IOException e){
//                System.out.print("Exception");
//            }
//            return word;
//        }
//        public static void set_string_in_file(String word){
//            try (FileWriter file = new FileWriter("word.txt")) {
//                for(int i = 0 ; i < word.length() ; ++i){
//                    file.write((int)word.charAt(i));                    
//                }
//            }
//            catch(IOException e){
//                System.out.print("Exception");
//            }
//        }
        public static void set_tags_in_file(ArrayList<Tag>Tags){
            try (FileWriter file = new FileWriter("Tags.txt")) {
                for(int i = 0 ; i < Tags.size() ; ++i){
                    file.write((byte)Tags.get(i).position);
                    file.write((byte)Tags.get(i).length);
                    int x = (int)Tags.get(i).next;
                    file.write((byte)x);
                }
                file.close();
            }
            catch(IOException e){
                System.out.print("Exception");
            }
        }
        public static ArrayList<Tag> get_tags_from_file(){
            ArrayList<Tag> Tags = new ArrayList<Tag>();
            try {
                Tag t = new Tag();
                Path path = Paths.get("Tags.txt");
                byte[] Bytes = Files.readAllBytes(path);
                for(int i = 0 ; i < Bytes.length ; ++i){
                    if(i%3 == 0)
                        t.position = Bytes[i];
                    else if(i%3 == 1)
                        t.length = Bytes[i];
                    else {
                        t.next = (char)Bytes[i];
                        Tags.add(t);
                        t = new Tag();
                    }
                }
//                for(int i = 0 ; i < Tags.size(); ++i){
//			System.out.print("<"+Tags.get(i).position+","+Tags.get(i).length+","+Tags.get(i).next+">");
//		}
            }
            catch(IOException e){
                System.out.print("Exception");
            }
           return Tags; 
        }
        
        public static int recursion(String word , String repeated_str, int point){
             int counter = 1; // to count the number of repeated sequence
             while( repeated_str.length()+point <= word.length() && word.substring(point,repeated_str.length()+point).equals(repeated_str)){
                 counter++;
                 point = repeated_str.length()+point; // to skip the length of the repeated_str string  to get new string to check it
             
             }
             return counter;
        }
        // the compression function
	public static ArrayList<Tag> compression (String word){
		ArrayList<Tag> Tags = new ArrayList<>(); // making array of tags
		String Current_sub=""; // string to hold the string which i will create tage for
                
		for(int i=0; i < word.length(); ++i ){ // strat the operation
                    Current_sub += word.charAt(i); // add the current char to my substring ^^^^
                    
                    String to_search_in = word.substring( 0,i-Current_sub.length()+1); // +1 here as "i" zero based and length one based
                    String exist ; // the existing part of the current string ^^^^
                    Tag t = new Tag(); // the new tag to add in my list
                    // check recursion case
                    if(to_search_in.length() != 0){
                        // i want to check if it can be repeated or not
                        // so if the length of "to_search_in" is zero it can't be repeated string
                        String repeated_part = Current_sub; // get the repeated part
                        String lastStr = to_search_in.substring(to_search_in.length()-repeated_part.length(),to_search_in.length()); // for recursion

                        if( repeated_part.equals(lastStr) ){

                           
                            int num = recursion( word , repeated_part, i+1); // get the number of repeated sub strings
                            
                            if(num >= 2){

                                t.position = Current_sub.length() ;
                                t.length = num*repeated_part.length(); 
                                
                                i += (num-1)*repeated_part.length(); 
                                if(i != word.length()-1){
                                    ++i;
                                }
                                else t.length--;
                                t.next = word.charAt(i);
                                Tags.add(t);
                                Current_sub = "";
                                continue;
                            } 
                        }
                    }
                    if( "".equals(to_search_in) )exist = to_search_in; // if i will search for the first char.
                    else
                    /*
                    abab
                    take the exist string from the current
                    when we have "ab" in the Current_sub
                    so we are sure that "a" is exist to i will take it in exist
                    */
                    exist = Current_sub.substring(0, Current_sub.length()-1); // ^^^^
                    if( !to_search_in.contains(Current_sub) ){ // if the current string dosn't exist befor
                        t.length = exist.length();
                        if("".equals(exist))
                            t.position = 0;
                        else 
                            t.position = (i - Current_sub.length()) - to_search_in.lastIndexOf(exist)+1;

                        t.next = word.charAt(i);
                        Tags.add(t);

                        Current_sub = "";
                    }
                    if (i == word.length()-1 && !"".equals(Current_sub)){
                        if( "".equals(to_search_in) )exist = to_search_in;
                        else
                            exist = Current_sub.substring(0, Current_sub.length());
                        t.length = exist.length()-1; 
                        if("".equals(exist))
                            t.position = 0;
                        else
                            t.position = (i - Current_sub.length()) - to_search_in.lastIndexOf(exist)+1;
                        
                        t.next = word.charAt(i);
                        Tags.add(t);
                        Current_sub = "";
                    }
		}
		for(int i = 0 ; i < Tags.size(); ++i){
			System.out.print("<"+Tags.get(i).position+","+Tags.get(i).length+","+Tags.get(i).next+">");
		}
                System.out.println();
		return Tags;
	}
        public static String decompression (ArrayList<Tag> Tags){
            String word = "";
            int pointer = 0;
            for(int i = 0 ; i < Tags.size() ; ++i){
                pointer = word.length() - Tags.get(i).position ;
                for(int j = pointer ; j < pointer+Tags.get(i).length ; ++j){
                    word += word.charAt(j);
                }
                word += Tags.get(i).next;
            }
            return word;
        }
	
}
class Tag{
	public int position , length;
	public char next;
}
