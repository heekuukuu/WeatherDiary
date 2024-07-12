package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.List;

public interface DateWeatherRepository  extends JpaRepository<DateWeather, LocalDate> {


    List<DateWeather> findAllByDate(LocalDate date);
}