package pt.tecnico.myDrive.domain;

import org.jdom2.Element;
import java.io.UnsupportedEncodingException;
import org.jdom2.DataConversionException;

import pt.tecnico.myDrive.exceptions.UserUnknownException;
import pt.tecnico.myDrive.exceptions.ImportDocumentException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.MethodDeniedException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.NoExtensionException;

import pt.tecnico.myDrive.visitors.GenericVisitor;

import pt.tecnico.myDrive.domain.App;
import org.joda.time.DateTime;
import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;

public class PlainFile extends PlainFile_Base {

  /** Placeholder for FenixFramework */
  protected PlainFile () {
    super();
  }

  public PlainFile(FileSystem fs, String name, Directory parent, User owner) {
    init(fs, fs.requestId(), name, parent, owner, "");
  }

  public PlainFile(FileSystem fs, String name, Directory parent, User owner, String data) {
    init(fs, fs.requestId(), name, parent, owner, data);
  }

  protected void init(FileSystem fs, Integer id, String name, Directory parent, User owner, String data) {
    super.init(fs, id, name, parent, owner);
    super.setData(data);
  }

  @Override
  public int getSize(){
    return super.getData().length();
  }

  @Override
  public File getFile(ArrayList<String> tokens, User user) {
    throw new NotADirectoryException(getName());
  }

  @Override
  public void execute(User user, String[] arguments) {
    FileSystem fs = getFileSystem$6p();
    String data = getData(user);
    String[] fileLines = data.split("\\r?\\n");

    for(String line : fileLines){
        String[] tokens = line.split("\\s+");
        String path = tokens[0];
        String[] args = ArrayUtils.removeElement(tokens,0);

        File file = fs.getFileByPath(path, user, getParent());
        if (file instanceof App ) file.execute(user, args);
    }
  }


  @Override
  public <T> T accept(GenericVisitor<T> v){
    return v.visit(this);
  }

  @Override
  public void setData(String data) {
    throw new MethodDeniedException();
  }

  @Override
  public String getData() {
    throw new MethodDeniedException();
  }

  public void setData(String data, User user) {
    user.checkWritePermissions(this);
    super.setData(data);
    touch();
  }

  public String getData(User user) {
    user.checkReadPermissions(this);
    return super.getData();
  }

  @Override
  public String toString(){
    return "- " + getUserPermission() + getOthersPermission() + " " + getName();
  }

}
