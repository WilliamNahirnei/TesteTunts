package exceptions;
public class DataException extends Exception {
    @Override
    public String getMessage(){
      return "Input Students data is invalid";
    }
  }
