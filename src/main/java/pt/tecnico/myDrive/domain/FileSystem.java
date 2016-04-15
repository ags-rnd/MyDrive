package pt.tecnico.myDrive.domain;

// Domain specific imports
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.File;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.ist.fenixframework.DomainRoot;

// Domain Exceptions
import pt.tecnico.myDrive.exceptions.*;

// Domain visitors
import pt.tecnico.myDrive.visitors.DirectoryVisitor;
import pt.tecnico.myDrive.visitors.PlainFileVisitor;
import pt.tecnico.myDrive.visitors.AppVisitor;
import pt.tecnico.myDrive.visitors.LinkVisitor;
import pt.tecnico.myDrive.visitors.XMLExporterVisitor;

// Jdom2
import org.jdom2.Element;
import org.jdom2.Document;

// IO
import java.io.UnsupportedEncodingException;

// Fenix Framework
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

// JodaTime
import org.joda.time.DateTime;

// Loggers
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

// Util imports
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import java.math.BigInteger;

public class FileSystem extends FileSystem_Base {

  private static final Logger log = LogManager.getRootLogger();


  /**
   * FileSystem temporary state variables
   * _loggedUser: keeps track of the user logged in
   * _currentDirectory: keeps track of the current navigation directory
   */

  private Login _login;

  private FileSystem() {
    log.trace("Constructing new FileSystem");
    super.setRoot(FenixFramework.getDomainRoot());
    try {
      init();
    } catch(RootDirectoryNotFoundException e) {
      System.out.println("-- Couldn't find Root Directory. Rebuilding File System");
      reset();
    }
  }

  /**
   * @return current instance of FileSystem if stored, or a new FileSystem otherwise
   */
  public static FileSystem getInstance() {
    log.trace("FileSystem instance requested");
    FileSystem fs = FenixFramework.getDomainRoot().getFileSystem();

    if (fs != null) {
      log.trace("Returning existing FileSystem instance");
      try {
        fs.init();
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
      return fs;
    }

    log.trace("Returning new FileSystem instance");
    return new FileSystem();
  }

  public void cleanup() {
    try{
      File file = getFileByPath("/", getRootUser(), super.getRootDirectory());
      removeFile(file, getRootUser());
      super.getFilesSet().clear();
    }catch (InsufficientPermissionsException | FileUnknownException | NotADirectoryException e){
      e.printStackTrace();
    }
    for (Login login: super.getLoginsSet())
      login.remove();
    for (User u: super.getUsersSet()){
      u.remove();
    }
  }

  /**
   * Resets the filesystem. Wipes all data stored in it and creates its initial
   * data.
   */
  @Atomic
  public void reset() {
    cleanup();
    cleanInit();
  }

  /**
   * Does basic FileSystem initialization
   */
  public void init() {
    /**
     * Creation of root directory and home folder if there are no files,
     * means we're initializing a new filesystem
     * If the filesystem is initialized we search for the root user and
     * root directory
     */

    if (super.getFilesSet().size() == 0) {
      cleanInit();
    } else{
      log.trace("Initializing existing FileSystem");
      if (super.getRootDirectory() == null) {
        throw new RootDirectoryNotFoundException();
      }
    }
    log.trace("Finished FileSystem initialization");
  }


  /**
   * Creates the initial data for the filesystem, such as, root user, root
   * directory and root's user home directory.
   */
  private void cleanInit() {
    log.trace("Initializing new FileSystem");
    super.setIdCounter(0);

    log.trace("Creating root user");
    super.setRootUser(new RootUser(this));

    log.trace("Creating root directory");
    super.setRootDirectory(new RootDirectory(this, "/", getRootUser()));


    log.trace("Creating home directory");
    Directory homeDir = createDirectory("home",super.getRootDirectory(),getRootUser());

    getRootUser().setHomeDirectory(createDirectory("root", homeDir, getRootUser()));

  }

  /**
   *
   * @param user
   * @return True if user is the root user
   */
  public boolean isRoot(User user) {
    return user == getRootUser();
  }

  /**
   * Searches Users Set by username (since username is unique) to find a specified user.
   * Returns null if no user is found. Does not throw exception here
   */
  public User getUserByUsername(String username) {
    for (User user: super.getUsersSet()) {
      if (user.getUsername().equalsIgnoreCase(username)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Searches Logins Set by User's username (since username is unique) to find a specific Login.
   * Returns null if no login is found.
   */
  private Login getLoginByUser(User user){
    for(Login login: super.getLoginsSet()){
      if(login.getUser().equals(user))
        return login;
    }
    return null;
  }

  /**
   * Verifies if a user exists by its username. Usernames are unique
   */
  private Boolean userExists(String username) {
    return (getUserByUsername(username)!=null);
  }

  private Boolean userExists(User user) {
    return userExists(user.getName());
  }

  @Override
  public void setIdCounter(Integer id) {
    throw new MethodDeniedException();
  }

  @Override
  public Integer getIdCounter() {
    throw new MethodDeniedException();
  }

  @Override
  public void setRoot(DomainRoot dr) {
    throw new MethodDeniedException();
  }

  @Override
  public void addUsers(User user) {
    throw new MethodDeniedException();
  }

  @Override
  public void addFiles(File file){
    throw new MethodDeniedException();
  }

  @Override
  public void addLogins(Login login){
    throw new MethodDeniedException();
  }

  @Override
  public void removeUsers(User user) {
    throw new MethodDeniedException();
  }

  @Override
  public void removeFiles(File file) {
    throw new MethodDeniedException();
  }

  @Override
  public void removeLogins(Login login) {
    throw new MethodDeniedException();
  }

  @Override
  public Set<Login> getLoginsSet() {
    throw new MethodDeniedException();
  }

  @Override
  public Set<File> getFilesSet() {
    throw new MethodDeniedException();
  }

  @Override
  public Set<User> getUsersSet() {
    throw new MethodDeniedException();
  }

  @Override
  public void setRootUser(RootUser rootUser) {
    throw new MethodDeniedException();
  }

  @Override
  public RootDirectory getRootDirectory() {
    throw new MethodDeniedException();
  }

  @Override
  public void setRootDirectory(RootDirectory rootDirectory) {
    throw new MethodDeniedException();
  }


  /* ****************************************************************************
   * |                 FileSystem's Users creation methods                       |
   * ****************************************************************************
   */
  /**
   * Creates a new user, checking for username constraints
   * Also creates its home directory
   * @return the created User
   */
  public User createUser(String username, String name, String password) {
    if (userExists(username))
      throw new UserExistsException(username);

    User user = new User(this, username, name, password);

    /**
     * TODO: Solve error throwing here. Exceptions shouldn't happen;
     * Should be handled elsewhere
     */
    log.trace("Adding user " + username);
    try {
      Directory home = assertDirectory(super.getRootDirectory().getFileByName("home"));
      Directory userHome = createDirectory(username, home, user);
      user.setHomeDirectory(userHome);
    } catch(FileUnknownException e) {
      System.out.println(e.getMessage());
    } catch(NotADirectoryException e) {
      System.out.println(e.getMessage());
    }


    log.trace("Added user " + username);

    return user;
  }

  /* ****************************************************************************
   * |                 FileSystem's Files creation methods                       |
   * ****************************************************************************
   */

  public int requestId() {
    super.setIdCounter(super.getIdCounter()+1);
    return super.getIdCounter();
  }

  private Directory createDirectory(String name, Directory parent, User owner) {
    return parent.createDirectory(name, owner);
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner) {
    return parent.createPlainFile(name, owner);
  }

  private PlainFile createPlainFile(String name, Directory parent, User owner, String data) {
    return parent.createPlainFile(name, owner, data);
  }

  private App createApp(String name, Directory parent, User owner) {
    return parent.createApp(name, owner);
  }

  private Link createLink(String name, Directory parent, User owner, String data) {
    return parent.createLink(name, owner, data);
  }

  private void removeFile(File file, User user) {
    file.remove(user);
  }

  private void removeFile(String filename, User user, Directory currentDirectory) {
    currentDirectory.remove(filename, user);
  }

  /* ****************************************************************************
   * |                     Public File creation methods                         |
   * ****************************************************************************
   */

  public Directory getHomeDirectory() {
    /**
     * TODO:XXX:FIXME DO PROPER CHECKING AND EXCEPTION HANDLING
     */
    try {
      return (Directory) super.getRootDirectory().getFileByName("home");
    }catch(FileUnknownException e){
      throw new RuntimeException("WRONG FILE STRUCTURE");
    }
  }

  /**
   * Changes current working directory
   */
  public void changeDirectory(String dirName, User user, Directory directory) {
    Directory d = assertDirectory(getFileByPath(dirName, user, directory));
    _login.setCurrentDirectory(d, user);
  }


  /**
   * ----------------------------------------------------
   * Public FileSystem usage methods
   * Allows execution of several commands on the current FileSystem
   */

  /**
   * @return current working directory path
   */
  public String listPath(Directory directory) {
    return directory.getPath();
  }

  /**
   * @return current working directory listing (files)
   */
  public String listDirectory(Directory directory, User user) {
    return directory.listFilesAll();
  }

  /**
   * @return result of executing file
   */
  public String executeFile(String path, User user, Directory directory) {
    File file = getFileByPath(path, user, directory);
    /**
     * TODO::XXX:FIX PERMISSIONS
     */
    // checkExecutionPermissions(user, file);
    return file.execute(user);
  }

  /* ****************************************************************************
   * |                          Operations by Path                              |
   * ****************************************************************************
   */

  /**
   * Get a file by its path.
   *
   * @param path
   * @param user
   * @param directory
   * @return The file at the end of the path.
   */
  public File getFileByPath(String path, User user, Directory directory) {
    if (path.equals("/")) return super.getRootDirectory();

    ArrayList<String> tokensList = processPath(path);

    // Absolute or relative?
    if (path.charAt(0) == '/') {
      tokensList.remove(0);
      return super.getRootDirectory().getFile(tokensList, user);
    } else {
      return directory.getFile(tokensList, user);
    }
  }

  public ArrayList<String> processPath(String path) {
    if(path.charAt(path.length()-1) == '/') {
      path = path.substring(0, path.length()-1);
    }

    String [] tokens = path.split("/");

    return new ArrayList<String>(Arrays.asList(tokens));
  }

  /**
   * @param path
   * @param user
   * @param directory
   * @return A string containing a simple list of files
   */

  public String listFileByPathSimple(String path, User user, Directory directory) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory d = getFileByPath(path, user, directory).accept(dv);
    return d.listFilesSimple();
  }


  /**
   * @param path
   * @param user
   * @param directory
   * @return A string containing a list of files with all of their properties.
   */
  public String listFileByPathAll(String path, User user, Directory directory) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory d = getFileByPath(path, user, directory).accept(dv);
    return d.listFilesAll();
  }

  /**
   * Verifies if a file is valid to export
   *
   * @param File
   */
  public Boolean isFileExportValid(File f) {
    DirectoryVisitor isDirectory = new DirectoryVisitor();

    if(f.getOwner().getUsername().equals("root")){
      Directory dir = f.accept(isDirectory);
      if(dir != null){
        if(dir.getSize() != 2) return false;
      }
    }
    return true;
  }

  /**
   * Creates a document, with the data in the FileSystem, in XML
   */
  public Document xmlExport() {
    // FIXME TODO
    /** XMLExporterVisitor xml = new XMLExporterVisitor(); */
    /** Element mydrive = new Element("myDrive"); */
    /** Document doc = new Document(mydrive); */

    /** for (User u: getUsersSet()){ */
    /**   if(!u.getUsername().equals("root")) */
    /**     mydrive.addContent(u.xmlExport()); */
    /** } */
    /** for (File f: getFilesSet()){ */

    /**   if(isFileExportValid(f)) mydrive.addContent(f.accept(xml)); */

    /** } */
    /** return doc; */
    return null; //todo remove that <<<<<<<<
  }

  public void xmlImportUser(Element userElement) throws UnsupportedEncodingException {
    // FIXME TODO
    /** String username = new String(userElement.getAttribute("username").getValue().getBytes("UTF-8")); */

    /** Element nameElement = userElement.getChild("name"); */
    /** String name; */
    /** if (nameElement != null) name = new String(nameElement.getText().getBytes("UTF-8")); */
    /** else name = username; */

    /** Element pwdElement = userElement.getChild("password"); */
    /** String pwd; */
    /** if (pwdElement != null) pwd = new String(pwdElement.getText().getBytes("UTF-8")); */
    /** else pwd = username; */

    /** createUser(username,name,pwd); */
  }

  public void xmlImportDir(Element dirElement) throws UnsupportedEncodingException {
    // FIXME TODO
    /** String name = new String(dirElement.getChild("name").getText().getBytes("UTF-8")); */
    /** String path = new String(dirElement.getChild("path").getText().getBytes("UTF-8")); */
    /** path = path + "/" + name; */
    /** Directory dir = createDirectoryByPath(path, getRootUser(), getRootDirectory()); */

    /** Element owner = dirElement.getChild("owner"); */
    /** User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8"))); */
    /** dir.setOwner(u); */

    /** dir.xmlImport(dirElement); */
  }

  public void xmlImportPlain(Element plainElement) throws UnsupportedEncodingException {
    // FIXME TODO
    /** String name = new String(plainElement.getChild("name").getText().getBytes("UTF-8")); */
    /** String path = new String(plainElement.getChild("path").getText().getBytes("UTF-8")); */
    /** path = path + "/" + name; */
    /** PlainFile plain = createPlainFileByPath(path, getRootUser(), getRootDirectory()); */

    /** Element owner = plainElement.getChild("owner"); */
    /** User u = getUserByUsername(new String(owner.getText().getBytes("UTF-8"))); */
    /** plain.setOwner(u); */

    /** plain.xmlImport(plainElement); */
  /** } */

  /** public void xmlImport(Element firstElement) { */
    // FIXME TODO
    /** try { */
    /**   for (Element userElement: firstElement.getChildren("user")) */
    /**     xmlImportUser(userElement); */

    /**   for (Element dirElement: firstElement.getChildren("dir")) */
    /**     xmlImportDir(dirElement); */

    /**   for (Element plainElement: firstElement.getChildren("plain")) */
    /**     xmlImportPlain(plainElement); */

    /** } catch (UnsupportedEncodingException |  FileExistsException | UserUnknownException | ImportDocumentException | UserExistsException | InvalidUsernameException e) { */
    /**   System.out.println("Error in import filesystem"); */
    /** } */
  }

  /* ****************************************************************************
   * |                           Asserting methods                              |
   * ****************************************************************************
   */

  /**
   * Verifies if the file f is a directory and gets the corresponding directory
   * @return Directory corresponding to f argument, or null if its not a Directory
   */
  public Directory assertDirectory(File f) {
    DirectoryVisitor dv = new DirectoryVisitor();
    Directory dir = f.accept(dv);
    if (dir == null) {
      throw new NotADirectoryException(f.getName());
    }
    return dir;
  }

  public PlainFile assertPlainFile(File f) {
    PlainFileVisitor pfv = new PlainFileVisitor();
    PlainFile pf = f.accept(pfv);
    if (pf == null)
      throw new NotAPlainFileException(f.getName());
    else
      return pf;
  }

  public App assertApp(File f) {
    AppVisitor av = new AppVisitor();
    App a = f.accept(av);
    if (a == null)
      throw new NotAAppException(f.getName());
    else
      return a;
  }

  public Link assertLink(File f) {
    LinkVisitor lv = new LinkVisitor();
    Link l = f.accept(lv);
    if (l == null) {
      throw new NotALinkException(f.getName());
    }
    return l;
  }


  /* ****************************************************************************
   * |                           Token Handling                                 |
   * ****************************************************************************
   */

  /**
   * @param token
   * @return The login which holds token except if it doesn't exist, in that
   * case, null is returned.
   */
  public Login getLoginByToken(long token) {
    for (Login login : super.getLoginsSet()) {
      if (login.compareToken(token))
        return login;
    }
    return null;
  }

  /**
   * @param token
   * @return The user which holds token, except if it doesn't exist, in that
   * case, null is returned.
   *
   */
  public User getUserByToken(long token) {
    Login login = getLoginByToken(token);
    return login != null ? login.getUser() : null;
  }


  /**
   * A token is unique if there's no login that holds it
   * @param token
   * @return
   */
  public boolean existsToken(long token) {
    return getLoginByToken(token) != null;
  }

  /**
   * Checks whether a token is valid or not.
   * A token is valid if it has a login which holds it and hasn't expired
   *
   * @param token
   * @return
   */
  public boolean isValidToken(long token) {
    Login login = getLoginByToken(token);
    return login != null && !login.hasExpired();
  }


  /**
   * Checks the validity of a token
   *
   * @param token
   * @return Returns true if the login which holds token hasn't expired, false
   * otherwise
   */
  private void updateSession(long token) {
    if (!isValidToken(token)) {
      endSession();
      log.warn("Invalid Token.");
      throw new InvalidTokenException();
    }

    Login login = getLoginByToken(token);
    initSession(login);
  }

  private void endSession() {
    _login = null;
  }

  private void initSession(Login login) {
    _login = login;
    login.extendToken();
  }


  /**
   * Cleans up expired logins.
   */
  private void cullLogins(){
    for (Login login: super.getLoginsSet()){
      if(login.hasExpired())
        login.remove();
    }

  }

  /* ****************************************************************************
   * |                              Services                                    |
   * ****************************************************************************
   */

  public void createFile(String name, String type, String content, long token) {
    updateSession(token);

    if(content.equals("")) createFileWithoutContent(name, type, _login.getUser(), _login.getCurrentDirectory());
    else createFileWithContent(name, type, content, _login.getUser(), _login.getCurrentDirectory());
  }

  private void createFileWithoutContent(String name, String type, User user, Directory directory) {
    Directory current = _login.getCurrentDirectory();
    switch(type.toLowerCase()){
      case "directory":
        createDirectory(name, current, user);
        break;

      case "plainfile":
        createPlainFile(name, current, user);
        break;

      /*case "app":
        createApp(name);
        break; TODO IN THIRD DELIVER*/

      case "link":
        throw new CreateLinkWithoutContentException();
    }
  }

  private void createFileWithContent(String name, String type, String data, User user, Directory directory) {
    Directory current = _login.getCurrentDirectory();
    switch(type.toLowerCase()){
      case "directory":
        throw new CreateDirectoryWithContentException();

      case "plainfile":
        createPlainFile(name, current, user, data);
        break;

      /*case "app":
        App a = createApp(name, user, directory);
        a.setData(data);
        break;TODO IN THIRD DELIVER*/

      case "link":
        createLink(name, current, user, data);
        break;
    }
  }
  /**
   * Logins a user into the filesystem, changing current directory to home directory
   */
  private long login(User user, String password) {
    if (user.verifyPassword(password)) {
      cullLogins();
      Long token = new BigInteger(64, new Random()).longValue();
      // If the token is not unique we keep generating
      while(existsToken(token)) {
        token = new BigInteger(64, new Random()).longValue();
      }

      _login = new Login(this, user, user.getHomeDirectory(), token);
      super.addLogins(_login);
      return token;
    } else { // if password was incorrect;
      throw new WrongPasswordException(user.getUsername());
    }
  }

  public long login(String username, String password) {
    log.trace("Logging in");
    if (!userExists(username)) {
      throw new UserUnknownException(username);
    }
    return login(getUserByUsername(username), password);
  }

  public String readFile(long token, String filename) {
    updateSession(token);
    File file = getFileByPath(filename, _login.getUser(), _login.getCurrentDirectory());
    PlainFile pf = assertPlainFile(file);
    return pf.getData(_login.getUser());
  }

  public void writeFile(long token, String path, String content) {
    updateSession(token);

    File file = getFileByPath(path,_login.getUser(), _login.getCurrentDirectory());

    PlainFile pf = assertPlainFile(file);
    pf.setData(content, _login.getUser());
  }

  public void deleteFile(long token, String filename) {
    updateSession(token);
    removeFile(filename, _login.getUser(), _login.getCurrentDirectory());
  }

  public String changeDirectory(long token, String dirpath) {
    updateSession(token);
    changeDirectory(dirpath, _login.getUser(), _login.getCurrentDirectory());
    return _login.getCurrentDirectory().getPath();
  }

  public String listCurrentDirectory(long token) {
    updateSession(token);
    return listDirectory(_login.getCurrentDirectory(), _login.getUser());
  }
}
