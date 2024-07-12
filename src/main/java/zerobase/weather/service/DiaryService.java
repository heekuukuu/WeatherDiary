package zerobase.weather.service;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apikey;
    private final DateWeatherRepository dateWeatherRepository;
    private final DiaryRepository diaryRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DateWeatherRepository dateWeatherRepository, DiaryRepository diaryRepository) {
        this.dateWeatherRepository = dateWeatherRepository;
        this.diaryRepository = diaryRepository;
    }
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    //매일 한시간마다 날씨정보 저장
    public void saveWeatherDate(){
        logger.info("오늘도 날씨데이터 잘가져온듯");
        dateWeatherRepository.save(getWeatherFromApi());

    }

      @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        //  open weather map 에서 날씨 데이터가져오기
        DateWeather dateWeather =  getDateWeather(date);

     //파싱된데이터 + 일기값 우리 db에 넣기
        Diary nowdiary = new Diary();
        nowdiary.setDateWeather(dateWeather);
        nowdiary.setText(text);
        diaryRepository.save(nowdiary);
        logger.info("end to create diary");
          //logger.error();
          //logger.warn();
    }
     private DateWeather getWeatherFromApi(){
         //  open weather map 에서 날씨 데이터가져오기
         String weatherData = getWeatherString();

         //받아온 날씨 json 파싱하기
         Map<String, Object> parseWeather = parseWeather(weatherData);
         DateWeather dateWeather = new DateWeather();
         dateWeather.setDate(LocalDate.now());
         dateWeather.setWeather(parseWeather.get("main").toString());
         dateWeather.setIcon(parseWeather.get("icon").toString());
         dateWeather.setTemperature((Double) parseWeather.get("temp"));
         return dateWeather;

    }
    private DateWeather getDateWeather(LocalDate date){
       List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
     if (dateWeatherListFromDB.size() ==0) {
         return getWeatherFromApi();
     }else {
         return dateWeatherListFromDB.get(0);
     }

    }


    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        if (date.isAfter(LocalDate.ofYearDay(3050,1))){
            throw new InvalidDate();
        }
      return diaryRepository.findByDate(date);

    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);

    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }


    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=souel&appid=" + apikey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
             br.close();
            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }
     private Map<String, Object> parseWeather(String jsonString){
         JSONParser jsonParser = new JSONParser();
         JSONObject jsonObject;

         try {
             jsonObject = (JSONObject) jsonParser.parse(jsonString);
         }catch (ParseException e){
             throw new RuntimeException(e);
         }
         Map<String, Object> resultMap = new HashMap<>();

         JSONObject mainData =  (JSONObject) jsonObject.get("main");
         resultMap.put("temp", mainData.get("temp"));
         JSONArray weatherArray =  (JSONArray) jsonObject.get("weather");
         JSONObject weatherData = (JSONObject) weatherArray.get(0);
         resultMap.put("main", weatherData.get("main"));
         resultMap.put("icon", weatherData.get("icon"));
         return resultMap;

    }


}