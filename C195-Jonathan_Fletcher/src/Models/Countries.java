/** @author Jonathan Fletcher*/
package Models;

/**
 * Model for Country objects.
 */
public class Countries {

    private final String country;
    private final int countryID;

    /**
     * Constructor for new Country objects.
     * @param country
     * @param countryID
     */
    public Countries(String country, int countryID) {
        this.country = country;
        this.countryID = countryID;
    }

    /** @return country name. */
    public String getCountry() {
        return this.country;
    }

    /** @return int country id. */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Overrides the standard 'toString' format.
     * @return country
     */
    @Override
    public String toString() {
        return country;
    }
}
