package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.visitors.GenericVisitor;

public class Directory extends Directory_Base {

  public Directory() {
    super();
  }

	/**
	 * TEMPORARY
	 */
	public int getSize(){
		return 0;
	}

  @Override
  public <T> T accept(GenericVisitor<T> v){
	  return v.visit(this);
  }

}
