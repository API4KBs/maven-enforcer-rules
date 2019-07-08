package mock;

import org.codehaus.plexus.logging.Logger;

public class SilentLog implements Logger {

  @Override
  public void debug(String message) {

  }

  @Override
  public void debug(String message, Throwable throwable) {

  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void info(String message) {

  }

  @Override
  public void info(String message, Throwable throwable) {

  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void warn(String message) {

  }

  @Override
  public void warn(String message, Throwable throwable) {

  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void error(String message) {

  }

  @Override
  public void error(String message, Throwable throwable) {

  }

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void fatalError(String message) {

  }

  @Override
  public void fatalError(String message, Throwable throwable) {

  }

  @Override
  public boolean isFatalErrorEnabled() {
    return false;
  }

  @Override
  public int getThreshold() {
    return 0;
  }

  @Override
  public void setThreshold(int threshold) {

  }

  @Override
  public Logger getChildLogger(String name) {
    return null;
  }

  @Override
  public String getName() {
    return null;
  }
}
