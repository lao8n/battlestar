package swarm_wars_library.comms;

/*
  CommsChannel - is a collection of shared information, packets
  When you get a packet it comes from the current packet list
  New packets that are set are put into a future packet list
  Update copies the future list into the current list
  This means that each entity works off the same information within one loop
 */
public class CommsChannel {

  private int pointer;
  private int numberOfReceivers;
  private CommsPacket[] currentPackets;
  private CommsPacket[] futurePackets;

  public CommsChannel(int numberOfReceivers) {
    pointer = 0;
    this.numberOfReceivers = numberOfReceivers;
    currentPackets = new CommsPacket[numberOfReceivers];
    futurePackets = new CommsPacket[numberOfReceivers];
  }

  // TODO this should probably return an immutable copy of the packet
  public CommsPacket getPacket(int i) {
    if(i >= currentPackets.length) throw new Error ("Getting packet out of range of CommsChannel..");
    CommsPacket packet = currentPackets[i] ;
    if(packet == null) throw new Error ("Getting packet from CommsChannel that is null, have you set it yet?");
    return packet;
  }

  public void setPacket(CommsPacket packet) {
    if(packet == null) throw new Error ("Setting a null packet in CommsChannel");
    if(i >= futurePackets.length) throw new Error ("Setting packet out of range of CommsChannel..");
    futurePackets[i] = packet;
  }

  public void incramentPointer() {
    pointer = (pointer + 1) % numberOfReceivers;
  }

  public int getNumberOfReceivers() {
    return numberOfReceivers;
  }

  public int getPointer() {
    return pointer;
  }

  public void update() {
    currentPackets = futurePackets.clone();
  }
}
