/** @author Jonathan Fletcher */
package Models;

 /**
  *  Model for report objects.
 */

public class Reports {

        private final String apptType;
        private final Integer numOfAppts;

        /**
         * Constructor for new report object used for 'types' report.
         * @param apptType Established types of appointments.
         * @param numOfAppts count of appointments in each type.
         */
        public Reports(String apptType, Integer numOfAppts) {
            this.apptType = apptType;
            this.numOfAppts = numOfAppts;
        }
    /** @return apptType name as string. */
        public String getApptType() {
            return apptType;
        }

    /** @return numOfAppts name as int. */
        public Integer getNumOfAppts() {
            return numOfAppts;
        }
    }

