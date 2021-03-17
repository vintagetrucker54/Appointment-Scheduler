package Models;

/** @author Jonathan Fletcher */

/**
 * Model for states/provinces (first-level-divisions) objects from the DB.
 */
public class firstLevelDivision {

    private final String division;
    private final int countryID;

    /**
     * Constructor for new FLD objects.
     * @param countryID ID of country
     * @param division FLD
     */
    public firstLevelDivision(int countryID, String division) {
        this.countryID = countryID;
        this.division = division;
    }

    /** @return division name as string. */
    public String getDivision() {
        return this.division;
    }

    /** @return int country id. */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Overrides standard 'toString' method.
     * @return FLD
     */
    @Override
    public String toString() {
        return division;
    }

}
