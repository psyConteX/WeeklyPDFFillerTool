package org.example;

import java.time.LocalDate;

public class Week {
        Week(LocalDate startDate) {
            this.startDate=startDate;
            this.friday=this.startDate.plusDays(4);
            this.endDate=this.startDate.plusDays(6);
        }
        LocalDate startDate;
        LocalDate friday;
        LocalDate endDate;

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getFriday() {
            return friday;
        }

        public void setFriday(LocalDate friday) {
            this.friday = friday;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

}
