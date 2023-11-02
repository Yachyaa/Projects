class HashTableFunctions
{
   // hash function weights
   // 9 integers, each in the range 0-5 to act as weights for the characters in the keys
   int [] weights = {3,3,0,1,4,0,1,2,2};
   // ADD YOUR WEIGHTS INSTEAD OF 1s

   // returns True if the hash table contains string s
   // return False if the hash table does not contain string s
   boolean find ( String s, int h, int hashTableSize, String [] hashTableArray )
   {
      if (hashTableArray.length == 0){
         return false;
         }
      else 
         for (int i = 0; i<hashTableSize; i++){
            if (hashTableArray[i].equals(s)){
               return true;
               }
   }
   return false;
 }
}
