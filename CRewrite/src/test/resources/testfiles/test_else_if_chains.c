void foo(char* s) { }

int main(void) {
   unsigned int alter;

   if(alter < 18) {
      foo("Sie sind noch nicht volljährig\n");
   }
   else if(alter > 18)  {
      foo("Sie sind volljährig\n");
   }
   else if(alter == 18) {
      foo("Den Führerschein schon bestanden?\n");
   }
   foo("Bye\n");
   return 0;
}
