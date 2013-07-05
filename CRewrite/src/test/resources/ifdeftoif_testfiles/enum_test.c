void main() {
enum  {
  DO_HISTORY = (1 * ((255 + 0) > 0)),
  SAVE_HISTORY = (2 * ((255 + 0) > 0) 

  #if definedEx(CONFIG_FEATURE_EDITING_SAVEHISTORY)
  * 1
  #endif
   
  #if !definedEx(CONFIG_FEATURE_EDITING_SAVEHISTORY)
  * 0
  #endif
  )
};
}