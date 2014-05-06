//+------------------------------------------------------------------+
//|                                                javaInterface.mq4 |
//|                        Copyright 2014, MetaQuotes Software Corp. |
//|                                              http://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "Copyright 2014,MetaQuotes Software Corp."
#property link      "http://www.mql5.com"
#property version   "1.00"
#property strict

// input parameters
extern int BarsMin=100;  
extern int TimeFrameSec = 5;


string FileNameCourse,FirstLine, CurLine,LastError,D = ";", FileNameTrades;
int FileHandler;
datetime LastData, CurrentTime;
double ArrayM1[][6];
//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+

int OnInit(){
  Print("init");
  
  //file for course
  int curTimeStamp = TimeLocal();
  string tmpFilenameSuffix = Period()+"_"+Symbol()+"_"+curTimeStamp+".csv";
  FileNameCourse = "ExportMT4\\data_"+tmpFilenameSuffix;
  FirstLine = "Date"+D+"Time"+D+"Open"+D+"High"+D+"Low"+D+"Close"+D+"Volume";
  FileDelete(FileNameCourse,FILE_WRITE);
  FileHandler = FileOpen(FileNameCourse,FILE_READ|FILE_CSV|FILE_WRITE|FILE_SHARE_READ);
  FileWrite(FileHandler,FirstLine);
  WriteHistory(FileHandler);
  FileClose(FileHandler);
  
  //file for trades
  FileNameTrades = "ExportMT4\\trades_"+tmpFilenameSuffix;
  FirstLine = "active"+D+"JavaID"+D+"MT4ID"+D+"tradeType"+D+"timeOpen"+D+"TimeClose"+D+"openingPrice"+D+"lotSize"+D+"volume"+D+"stopLoss"+D+"takeProfit";
  FileDelete(FileNameTrades,FILE_WRITE);
  FileHandler = FileOpen(FileNameTrades,FILE_READ|FILE_CSV|FILE_WRITE|FILE_SHARE_READ);
  FileWrite(FileHandler,FirstLine);
  WriteTrades(FileHandler);
  FileClose(FileHandler);
  
  
//--- create timer 
   EventSetTimer(TimeFrameSec);
      
//---
   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Write History                                           |
//+------------------------------------------------------------------+
  void WriteHistory(int FileHandler){
   double historyOpen[];
   int getHistoryOpen = CopyOpen(Symbol(),Period(),1,BarsMin,historyOpen);
   double historyClose[];
   int getHistoryClose = CopyClose(Symbol(),Period(),1,BarsMin,historyClose);
   double historyHigh[];
   int getHistoryHigh = CopyHigh(Symbol(),Period(),1,BarsMin,historyHigh);
   double historyLow[];
   int getHistoryLow = CopyLow(Symbol(),Period(),1,BarsMin,historyLow);
   long historyVolume[];
   int getHistoryVolume = CopyTickVolume(Symbol(),Period(),1,BarsMin,historyVolume);
   datetime historyTime[];
   int getHistoryTime = CopyTime(Symbol(),Period(),1,BarsMin,historyTime);
   LastData = historyTime[BarsMin-1];
   if(getHistoryOpen!=-1&& getHistoryClose!=-1&&getHistoryHigh!=-1&&getHistoryLow&&getHistoryVolume!=-1&&getHistoryTime!=-1){
      for(int i = 0;i<BarsMin;i++){
        WriteOneLineToFile(historyTime[i],historyOpen[i],historyHigh[i],historyLow[i],historyClose[i],historyVolume[i]);
      }
   }
  }
  
  //+------------------------------------------------------------------+
//| Write active Trades                                        |
//+------------------------------------------------------------------+
   void WriteTrades(int FileHandler){
   double historyOpen[];
   int getHistoryOpen = CopyOpen(Symbol(),Period(),1,BarsMin,historyOpen);
   double historyClose[];
   int getHistoryClose = CopyClose(Symbol(),Period(),1,BarsMin,historyClose);
   double historyHigh[];
   int getHistoryHigh = CopyHigh(Symbol(),Period(),1,BarsMin,historyHigh);
   double historyLow[];
   int getHistoryLow = CopyLow(Symbol(),Period(),1,BarsMin,historyLow);
   long historyVolume[];
   int getHistoryVolume = CopyTickVolume(Symbol(),Period(),1,BarsMin,historyVolume);
   datetime historyTime[];
   int getHistoryTime = CopyTime(Symbol(),Period(),1,BarsMin,historyTime);
   LastData = historyTime[BarsMin-1];
   if(getHistoryOpen!=-1&& getHistoryClose!=-1&&getHistoryHigh!=-1&&getHistoryLow&&getHistoryVolume!=-1&&getHistoryTime!=-1){
      for(int i = 0;i<BarsMin;i++){
        WriteOneLineToFile(historyTime[i],historyOpen[i],historyHigh[i],historyLow[i],historyClose[i],historyVolume[i]);
      }
   }
  }
  
//+------------------------------------------------------------------+
//| Writes to the file and appends Data to the end                      |
//+------------------------------------------------------------------+
  void WriteOneLineToFile(datetime time, double open, double high, double low, double close, long volume){ 
   int year = TimeYear(time);
   int month = TimeMonth(time);
   int day = TimeDay(time);
   int hour = TimeHour(time);
   int minute = TimeMinute(time);
   string output = year+"."+month +"."+day+D+hour+":"+minute+D+open+D+high+D+low+D+close+D+volume;
   FileSeek(FileHandler,0,SEEK_END);
   FileWrite(FileHandler,output);
  }
  
//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason){
//--- destroy timer
   EventKillTimer();
      
  }
//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick() {  
   datetime time = iTime(Symbol(),Period(),1);
   if(time!=LastData){
      LastData = time;
      double open = iOpen(Symbol(),Period(),1);
      double high = iHigh(Symbol(),Period(),1);
      double low = iLow(Symbol(),Period(),1);
      double close = iClose(Symbol(),Period(),1);
      long volume = iVolume(Symbol(),Period(),1);
      FileHandler = FileOpen(FileNameCourse,FILE_CSV|FILE_READ|FILE_WRITE);
         if(FileHandler==-1){
            LastError = GetLastError();
            Print("Error opening file: "+ErrorDescription(LastError));
         }else{
            WriteOneLineToFile(time,open,high,low,close,volume);
            FileClose(FileHandler);
      }
   }
  }
//+------------------------------------------------------------------+
//| Timer function                                                   |
//+------------------------------------------------------------------+
void OnTimer() {

   
  datetime timeCur = TimeCurrent();
  if(isPaused(timeCur)){
      return;
  }
  
   
  }
//+------------------------------------------------------------------+
//| ChartEvent function                                              |
//+------------------------------------------------------------------+
void OnChartEvent(const int id,
                  const long &lparam,
                  const double &dparam,
                  const string &sparam) {
//---
   
  }
  
  
bool isPaused(datetime timeToCheck){
       if(timeToCheck == CurrentTime){
            return true;
         }else{
            CurrentTime = timeToCheck;
            return false;
         }
  }
  
//+------------------------------------------------------------------+
//---- codes returned from trade server                              |
//+------------------------------------------------------------------+
string ErrorDescription(int error_code){
   string error_string;
//----
   switch(error_code)
     {
      case 0:
      case 1:   error_string="no error";                                                  break;
      case 2:   error_string="common error";                                              break;
      case 3:   error_string="invalid trade parameters";                                  break;
      case 4:   error_string="trade server is busy";                                      break;
      case 5:   error_string="old version of the client terminal";                        break;
      case 6:   error_string="no connection with trade server";                           break;
      case 7:   error_string="not enough rights";                                         break;
      case 8:   error_string="too frequent requests";                                     break;
      case 9:   error_string="malfunctional trade operation";                             break;
      case 64:  error_string="account disabled";                                          break;
      case 65:  error_string="invalid account";                                           break;
      case 128: error_string="trade timeout";                                             break;
      case 129: error_string="invalid price";                                             break;
      case 130: error_string="invalid stops";                                             break;
      case 131: error_string="invalid trade volume";                                      break;
      case 132: error_string="market is closed";                                          break;
      case 133: error_string="trade is disabled";                                         break;
      case 134: error_string="not enough money";                                          break;
      case 135: error_string="price changed";                                             break;
      case 136: error_string="off quotes";                                                break;
      case 137: error_string="broker is busy";                                            break;
      case 138: error_string="requote";                                                   break;
      case 139: error_string="order is locked";                                           break;
      case 140: error_string="long positions only allowed";                               break;
      case 141: error_string="too many requests";                                         break;
      case 145: error_string="modification denied because order too close to market";     break;
      case 146: error_string="trade context is busy";                                     break;
      //---- mql4 errors
      case 4000: error_string="no error";                                                 break;
      case 4001: error_string="wrong function pointer";                                   break;
      case 4002: error_string="array index is out of range";                              break;
      case 4003: error_string="no memory for function call stack";                        break;
      case 4004: error_string="recursive stack overflow";                                 break;
      case 4005: error_string="not enough stack for parameter";                           break;
      case 4006: error_string="no memory for parameter string";                           break;
      case 4007: error_string="no memory for temp string";                                break;
      case 4008: error_string="not initialized string";                                   break;
      case 4009: error_string="not initialized string in array";                          break;
      case 4010: error_string="no memory for array\' string";                             break;
      case 4011: error_string="too long string";                                          break;
      case 4012: error_string="remainder from zero divide";                               break;
      case 4013: error_string="zero divide";                                              break;
      case 4014: error_string="unknown command";                                          break;
      case 4015: error_string="wrong jump (never generated error)";                       break;
      case 4016: error_string="not initialized array";                                    break;
      case 4017: error_string="dll calls are not allowed";                                break;
      case 4018: error_string="cannot load library";                                      break;
      case 4019: error_string="cannot call function";                                     break;
      case 4020: error_string="expert function calls are not allowed";                    break;
      case 4021: error_string="not enough memory for temp string returned from function"; break;
      case 4022: error_string="system is busy (never generated error)";                   break;
      case 4050: error_string="invalid function parameters count";                        break;
      case 4051: error_string="invalid function parameter value";                         break;
      case 4052: error_string="string function internal error";                           break;
      case 4053: error_string="some array error";                                         break;
      case 4054: error_string="incorrect series array using";                             break;
      case 4055: error_string="custom indicator error";                                   break;
      case 4056: error_string="arrays are incompatible";                                  break;
      case 4057: error_string="global variables processing error";                        break;
      case 4058: error_string="global variable not found";                                break;
      case 4059: error_string="function is not allowed in testing mode";                  break;
      case 4060: error_string="function is not confirmed";                                break;
      case 4061: error_string="send mail error";                                          break;
      case 4062: error_string="string parameter expected";                                break;
      case 4063: error_string="integer parameter expected";                               break;
      case 4064: error_string="double parameter expected";                                break;
      case 4065: error_string="array as parameter expected";                              break;
      case 4066: error_string="requested history data in update state";                   break;
      case 4099: error_string="end of file";                                              break;
      case 4100: error_string="some file error";                                          break;
      case 4101: error_string="wrong file name";                                          break;
      case 4102: error_string="too many opened files";                                    break;
      case 4103: error_string="cannot open file";                                         break;
      case 4104: error_string="incompatible access to a file";                            break;
      case 4105: error_string="no order selected";                                        break;
      case 4106: error_string="unknown symbol";                                           break;
      case 4107: error_string="invalid price parameter for trade function";               break;
      case 4108: error_string="invalid ticket";                                           break;
      case 4109: error_string="trade is not allowed";                                     break;
      case 4110: error_string="longs are not allowed";                                    break;
      case 4111: error_string="shorts are not allowed";                                   break;
      case 4200: error_string="object is already exist";                                  break;
      case 4201: error_string="unknown object property";                                  break;
      case 4202: error_string="object is not exist";                                      break;
      case 4203: error_string="unknown object type";                                      break;
      case 4204: error_string="no object name";                                           break;
      case 4205: error_string="object coordinates error";                                 break;
      case 4206: error_string="no specified subwindow";                                   break;
      default:   error_string="unknown error";
     }
//----
   return(error_string);
  }