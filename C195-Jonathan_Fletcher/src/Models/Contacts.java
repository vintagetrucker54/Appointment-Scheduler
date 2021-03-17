/** @author Jonathan Fletcher. */
package Models;

 /**
  * Model for Contact objects.
 */
public class Contacts {

    private final int contactID;
    private final String contactName;
    private final String contactEmail;

    /**
     * Constructor for a Contact object.
     * @param contactID ID of established contacts
     * @param contactName Name of established contacts
     * @param contactEmail Email of established contacts
     */
    public Contacts(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /** @return contact id. */
    public int getContactID() {
        return contactID;
    }

    /** @return contact name. */
    public String getContactName() {
        return contactName;
    }

    /**
     * Overrides standard 'toString' method.
     * @return string in a user-friendly format
     */
    @Override
    public String toString() {
        return "[" + contactID + "] " + contactName;
    }
}
