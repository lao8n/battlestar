package swarm_wars_library.graphics;

import swarm_wars_library.engine.Vector2D;
import swarm_wars_library.map.Map;
import processing.core.PApplet;

public abstract class RenderMapObject{
  protected PApplet sketch;
  protected Vector2D objectRenderPosition = new Vector2D(0, 0);

  public RenderMapObject(PApplet sketch){
    this.sketch = sketch;
  }

  protected abstract void renderMapObject();

  public void update(Vector2D objectMapPosition, 
    Vector2D viewCentreMapPosition){

    this.setObjectRenderPosition(objectMapPosition, viewCentreMapPosition);
    if(this.objectRenderPosition.getX() >= 0 && 
       this.objectRenderPosition.getX() <= this.sketch.width &&
       this.objectRenderPosition.getY() >= 0 && 
       this.objectRenderPosition.getY() <= this.sketch.height){
        this.renderMapObject();
    }
  }

  private void setObjectRenderPosition(Vector2D objectMapPosition, 
    Vector2D viewCentreMapPosition){

    double viewCentreMapX;
    double viewCentreMapY;

    if(Map.getInstance().getMapWidth() - viewCentreMapPosition.getX() < 
       this.sketch.width /2){
      viewCentreMapX = Map.getInstance().getMapWidth() - this.sketch.width / 2;
    }
    else if (viewCentreMapPosition.getX() < this.sketch.width / 2){
      viewCentreMapX = this.sketch.width / 2;
    }
    else {
      viewCentreMapX = viewCentreMapPosition.getX();
    }

    if(Map.getInstance().getMapHeight() - viewCentreMapPosition.getY() < 
       this.sketch.height /2){
      viewCentreMapY = Map.getInstance().getMapHeight() - this.sketch.height / 2;
    }
    else if (viewCentreMapPosition.getY() < this.sketch.height / 2){
      viewCentreMapY = this.sketch.height / 2;
    }
    else {
      viewCentreMapY = viewCentreMapPosition.getY();
    }
    
    this.objectRenderPosition.setX(objectMapPosition.getX() - 
      viewCentreMapX + this.sketch.width / 2);
    this.objectRenderPosition.setY(objectMapPosition.getY() - 
      viewCentreMapY + this.sketch.height / 2);
  }

}