package org.mifos.application.meeting.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.exceptions.PersistenceException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeetingBOTest {

    @Mock
    FiscalCalendarRules fiscalCalendarRules;
    
    @Mock
    MasterPersistence masterPersistence;

    @Test
    public void testWeeklyMeetingInterval() {
        MeetingBO meeting = new MeetingBuilder().weekly().every(1).occuringOnA(WeekDay.THURSDAY).build();
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        DateTime paymentDate = new DateMidnight(2010, 2, 4).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 7).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 8).toDateTime(), paymentDate), is(false));                       
    }

    
    @Test
    public void testBiWeeklyMeetingInterval() {
        MeetingBO meeting = new MeetingBuilder().weekly().every(2).occuringOnA(WeekDay.THURSDAY).build();
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);

        DateTime paymentDate = new DateMidnight(2010, 2, 4).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 14).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 15).toDateTime(), paymentDate), is(false));                       
    }
    
    @Test
    public void testMonthlyOnDayOfMonthMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        DateTime paymentDate = new DateMidnight(2010, 2, 3).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 28).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 3, 1).toDateTime(), paymentDate), is(false));                       
    }
    
    @Test
    public void testBiMonthlyOnDayOfMonthMeetingInterval() throws MeetingException {
        MeetingBO meeting = new MeetingBuilder().monthly().every(2).buildMonthlyForDayNumber(20);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        DateTime paymentDate = new DateMidnight(2010, 2, 3).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 3, 31).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 4, 1).toDateTime(), paymentDate), is(false));                       
    }

    
    @Test
    public void testMonthlyMeetingInterval() throws MeetingException, PersistenceException {
        when(masterPersistence.retrieveMasterEntity(WeekDay.FRIDAY.getValue(),WeekDaysEntity.class, null)).thenReturn(
                new WeekDaysEntity(WeekDay.FRIDAY));
        when(masterPersistence.retrieveMasterEntity(RankType.THIRD.getValue(),RankOfDaysEntity.class, null)).thenReturn(
                new RankOfDaysEntity(RankType.THIRD));
        MeetingBO meeting = new MeetingBuilder(masterPersistence).monthly().every(1).buildMonthlyFor(RankType.THIRD, WeekDay.FRIDAY);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        DateTime paymentDate = new DateMidnight(2010, 2, 19).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 28).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 3, 1).toDateTime(), paymentDate), is(false));                       
    }
    
    @Test
    public void testBiMonthlyMeetingInterval() throws MeetingException, PersistenceException {
        when(masterPersistence.retrieveMasterEntity(WeekDay.FRIDAY.getValue(),WeekDaysEntity.class, null)).thenReturn(
                new WeekDaysEntity(WeekDay.FRIDAY));
        when(masterPersistence.retrieveMasterEntity(RankType.THIRD.getValue(),RankOfDaysEntity.class, null)).thenReturn(
                new RankOfDaysEntity(RankType.THIRD));
        MeetingBO meeting = new MeetingBuilder(masterPersistence).monthly().every(2).buildMonthlyFor(RankType.THIRD, WeekDay.FRIDAY);
        meeting.setFiscalCalendarRules(fiscalCalendarRules);
        when(fiscalCalendarRules.getStartOfWeekWeekDay()).thenReturn(WeekDay.MONDAY);
        DateTime paymentDate = new DateMidnight(2010, 2, 19).toDateTime();
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 1, 31).toDateTime(), paymentDate), is(false));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 2, 1).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 3, 31).toDateTime(), paymentDate), is(true));                       
        assertThat(meeting.queryDateIsInMeetingIntervalForFixedDate(new DateMidnight(2010, 4, 1).toDateTime(), paymentDate), is(false));                       
    }    
}
