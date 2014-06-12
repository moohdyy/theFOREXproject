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
extern int TimeFrameSec=20;
extern int UniqueID;

string FileNameCourse,FirstLine,CurLine,LastError,D=";",FileNameTrades,FileNameLog;
datetime LastData,CurrentTime;
bool isPaused;

//bugfix for debugging
class CFix { } ExtFix;
//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+

int OnInit()
  {
//log file
   FileNameLog=StringConcatenate("LOG_JavaInterface_",GetCurTime("Days"));
   writeToLogFile("Init started.");

//file for course
   int curTimeStamp=TimeLocal();
   string tmpFilenameSuffix=Period()+"_"+Symbol()+"_"+curTimeStamp+".csv";
   FileNameCourse="ExportMT4\\data_"+tmpFilenameSuffix;
   FirstLine="Date"+D+"Time"+D+"Open"+D+"High"+D+"Low"+D+"Close"+D+"Volume"+D+"Spread";
   FileDelete(FileNameCourse,FILE_WRITE);
   int FileHandler=FileOpen(FileNameCourse,FILE_READ|FILE_CSV|FILE_WRITE|FILE_SHARE_READ);
   if(FileHandler<0)
     {
      writeToLogFile("Error while opening File: "+FileNameCourse+",Error: "+ErrorDescription(GetLastError()));
        }else{
      writeToLogFile("File "+FileNameCourse+" opened.");
     }
   FileWrite(FileHandler,FirstLine);
//time of last bar
   datetime time=iTime(Symbol(),Period(),1);
   WriteHistory(FileHandler,time);
   FileClose(FileHandler);

//file for trades
   FileNameTrades="ExportMT4\\trades_"+tmpFilenameSuffix;
   FileDelete(FileNameTrades,FILE_WRITE);
   FileHandler=FileOpen(FileNameTrades,FILE_READ|FILE_CSV|FILE_WRITE|FILE_SHARE_READ);
   if(FileHandler<0)
     {
      writeToLogFile("Error while opening File: "+FileNameTrades+",Error: "+ErrorDescription(GetLastError()));
        }else{
      writeToLogFile("File "+FileNameTrades+" opened.");
     }
   WriteActiveTrades(FileHandler);
   FileClose(FileHandler);
//--- create timer for refresh of Trades

   isPaused=false;
   EventSetTimer(TimeFrameSec);

//---
   writeToLogFile("Init completed.");
   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Write History                                           |
//+------------------------------------------------------------------+
void WriteHistory(int FileHandler,datetime time)
  {
   writeToLogFile("WriteHistory started.");
   double historyOpen[];
   int getHistoryOpen=CopyOpen(Symbol(),Period(),time,BarsMin,historyOpen);
   double historyClose[];
   int getHistoryClose=CopyClose(Symbol(),Period(),time,BarsMin,historyClose);
   double historyHigh[];
   int getHistoryHigh=CopyHigh(Symbol(),Period(),time,BarsMin,historyHigh);
   double historyLow[];
   int getHistoryLow=CopyLow(Symbol(),Period(),time,BarsMin,historyLow);
   long historyVolume[];
   int getHistoryVolume=CopyTickVolume(Symbol(),Period(),time,BarsMin,historyVolume);
   datetime historyTime[];
   int getHistoryTime=CopyTime(Symbol(),Period(),time,BarsMin,historyTime);

// no spread data available for historical data, so assume actual spread
   double spread=(Ask-Bid)/Point();
   LastData=historyTime[BarsMin-1];
   if(getHistoryOpen!=-1 && getHistoryClose!=-1 && getHistoryHigh!=-1 && getHistoryLow && getHistoryVolume!=-1 && getHistoryTime!=-1)
     {
      for(int i=0;i<BarsMin;i++)
        {
         WriteOneOHLCLineToFile(FileHandler,historyTime[i],historyOpen[i],historyHigh[i],historyLow[i],historyClose[i],historyVolume[i],spread);
        }
     }
   writeToLogFile("WriteHistory completed.");
  }
//+------------------------------------------------------------------+
//| Write active Trades                                        |
//+------------------------------------------------------------------+
void WriteActiveTrades(int FileHandler)
  {
   writeToLogFile("WriteActiveTrades started.");
   for(int i=0;i<OrdersTotal();i++)
     {
      if(!OrderSelect(i,SELECT_BY_POS,MODE_TRADES))
        {
         writeToLogFile("Select order "+i+"failed: "+GetLastError());
         continue;
        }
      if(OrderSymbol()==Symbol() && OrderMagicNumber()==UniqueID)
        {
         bool active=false;
         datetime close=OrderCloseTime();
         if(close==0)
           { //if no closing time available, order is still open
            active=true;
           }
         if(active)
           {
            int javaId=-1;
            WriteOneTradeLineToFile(FileHandler,
                                    OrderTicket(),
                                    active,
                                    OrderType(),
                                    OrderOpenTime(),
                                    OrderCloseTime(),
                                    OrderOpenPrice(),
                                    OrderClosePrice(),
                                    OrderLots(),
                                    OrderTakeProfit(),
                                    OrderStopLoss());
           }
        }
     }

   writeToLogFile("WriteActiveTrades ended.");
  }
//+------------------------------------------------------------------+
//| Writes to the file and appends Data to the end                      |
//+------------------------------------------------------------------+
void WriteOneOHLCLineToFile(int FileHandler,datetime time,double open,double high,double low,double close,long volume,int spread)
  {
   int year=TimeYear(time);
   int month=TimeMonth(time);
   int day=TimeDay(time);
   int hour=TimeHour(time);
   int minute=TimeMinute(time);
   string output=year+"."+month+"."+day+D+hour+":"+minute+D+open+D+high+D+low+D+close+D+volume+D+spread;
   FileSeek(FileHandler,0,SEEK_END);
   FileWrite(FileHandler,output);
  }
//+------------------------------------------------------------------+
//| Writes to the file and appends Data to the end                      |
//+------------------------------------------------------------------+
void WriteOneTradeLineToFile(int FileHandler,
                             int mt4ID,
                             bool active,
                             int orderType,
                             datetime openTime,
                             datetime closeTime,
                             double openPrice,
                             double closePrice,
                             double lotSize,
                             double takeProfit,
                             double stopLoss)
  {
   string output=active+D+mt4ID+D+orderType+D+openTime+D+closeTime+D+openPrice+D+closePrice+D+lotSize+D+takeProfit+D+stopLoss;
   FileSeek(FileHandler,0,SEEK_END);
   FileWrite(FileHandler,output);
  }
//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason)
  {
//--- destroy timer
   EventKillTimer();

  }
//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
  {
   writeToLogFile("OnTick started.");
   datetime time=iTime(Symbol(),Period(),1);
   while(time!=LastData)
     {
      double open = iOpen(Symbol(),Period(),1);
      double high = iHigh(Symbol(),Period(),1);
      double low=iLow(Symbol(),Period(),1);
      double close= iClose(Symbol(),Period(),1);
      long volume = iVolume(Symbol(),Period(),1);
      double spread=(Ask-Bid)/Point;
      int FileHandler=FileOpen(FileNameCourse,FILE_CSV|FILE_READ|FILE_WRITE);
      if(FileHandler==-1)
        {
         int error=GetLastError();
         writeToLogFile("File "+FileNameCourse+" could not be opened: "+ErrorDescription(error)+"("+error+"). Sleeping 1 second.");
         sleepThisThread(3);
           }else{
         writeToLogFile("File "+FileNameCourse+" opened.");
         WriteOneOHLCLineToFile(FileHandler,time,open,high,low,close,volume,spread);
         FileClose(FileHandler);
         LastData=time;
        }
     }
   writeToLogFile("OnTick ended.");
  }
//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
void sleepThisThread(int seconds)
  {
   int time_waiting=TimeLocal()+seconds;
   while(TimeLocal()<time_waiting)
     {
     }
  }
//+------------------------------------------------------------------+
//| Timer function                                                   |
//+------------------------------------------------------------------+
void OnTimer()
  {
   if(isPaused) // if interface is still writing, dont go in this method
     {
      return;
        }else{
      isPaused=true;
      ReadAndApplyTrades();
      FileDelete(FileNameTrades,FILE_WRITE);
      int FileHandler=FileOpen(FileNameTrades,FILE_READ|FILE_CSV|FILE_WRITE|FILE_SHARE_READ);
      if(FileHandler<0)
        {
         writeToLogFile("Error while opening File: "+FileNameTrades+",Error: "+ErrorDescription(GetLastError()));
           }else{
         writeToLogFile("File "+FileNameTrades+" opened.");
        }
      WriteActiveTrades(FileHandler);
      FileClose(FileHandler);
      isPaused=false;
     }

  }
//+------------------------------------------------------------------+
//|  Reads the tradefile, opens and closes trades from javastrategy  |
//+------------------------------------------------------------------+
void ReadAndApplyTrades()
  {
   int FileHandler=FileOpen(FileNameTrades,FILE_READ|FILE_CSV,D);
   if(FileHandler==INVALID_HANDLE)
     {
      int error=GetLastError();
      writeToLogFile("File "+FileNameTrades+" could not be opened: "+ErrorDescription(error)+"("+error+")");
      return;
     }
   int counter=0;
   int slippage=1;
   while(!FileIsEnding(FileHandler))
     {
      //"active"+D+"MT4ID"+D+"tradeType"+D+"timeOpen"+D+"timeClose"+D+"openingPrice"+D+"closingPrice"+D+"lotSize"+D+"takeProfit"+D+"stopLoss";
      bool active=FileReadBool(FileHandler);
      int MT4ID=FileReadNumber(FileHandler);
      int tradeType=FileReadNumber(FileHandler);
      datetime timeOpen=FileReadDatetime(FileHandler);
      datetime timeClose=FileReadDatetime(FileHandler);
      double openingPrice = FileReadNumber(FileHandler);
      double closingPrice = FileReadNumber(FileHandler);
      double lotSize=FileReadNumber(FileHandler);
      double takeProfit=FileReadNumber(FileHandler);
      double stopLoss=FileReadNumber(FileHandler);
      if(MT4ID==0)
        {//new trade
         double stopLevel=MarketInfo(Symbol(),MODE_STOPLEVEL);
         if(tradeType==OP_BUY) // BUY
           {
            RefreshRates();
            double actualBid=Bid;
            if(stopLoss!=0)
              {
               if(actualBid-stopLoss<stopLevel)
                 {
                  stopLoss=actualBid-stopLevel;
                 }
              }
            if(takeProfit!=0)
              {
               if(takeProfit-actualBid<stopLevel)
                 {
                  takeProfit=stopLevel+actualBid;
                 }
              }
            int id=OrderSend(Symbol(),OP_BUY,lotSize,Ask,slippage,stopLoss,takeProfit,"",UniqueID,0,clrAqua);
            if(id==-1)
              {
               writeToLogFile(Symbol()+", TYPE:"+OP_BUY+", LOTSIZE"+lotSize+", "+Ask+", SLIPPAGE"+slippage+", STOPLOSS"+stopLoss+", TAKEPROFIT"+takeProfit+", UNIQUEID"+UniqueID+","+"Order opening failed: "+ErrorDescription(GetLastError()));
                 }else{
               writeToLogFile("Order "+id+" succesfully opened");
              }
              }else{ // SELL
            RefreshRates();
            double actualAsk=Ask;
            if(stopLoss>0)
              {
               if(stopLoss-actualAsk<stopLevel)
                 {
                  stopLoss=actualAsk+stopLevel;
                 }
                 }else{
               stopLoss=0;
              }
            if(takeProfit>0)
              {
               if(actualAsk-takeProfit<stopLevel)
                 {
                  takeProfit=actualAsk-stopLevel;
                 }
                 }else{
               takeProfit=0;
              }
            int id=OrderSend(Symbol(),OP_SELL,lotSize,Bid,slippage,stopLoss,takeProfit,"",UniqueID,0,clrAqua);
            if(id==-1)
              {
               writeToLogFile(Symbol()+","+OP_SELL+","+lotSize+","+Bid+","+slippage+","+stopLoss+","+takeProfit+","+UniqueID+","+"Order opening failed: "+ErrorDescription(GetLastError()));
                 }else{
               writeToLogFile("Order "+id+" succesfully opened");
              }
           }
           }else{
         if(!active) // sign to close a trade
           {
            bool success;
            if(tradeType==OP_BUY)
              {
               RefreshRates();
               success = OrderClose(MT4ID,lotSize,Bid,slippage,clrGreen);
                 }else{
               RefreshRates();
               success = OrderClose(MT4ID,lotSize,Ask,slippage,clrGreen);
              }

            if(success)
              {
               writeToLogFile("Order "+MT4ID+" succesfully closed.");
              }else{
                writeToLogFile("Order "+MT4ID+" closing failed with lot size "+lotSize+": "+ErrorDescription(GetLastError()));
             
              }
              
           }
        }
      counter=counter+1;
      writeToLogFile("Trade no "+counter+" in file "+FileNameTrades+" processed.");
     }
   FileClose(FileHandler);
  }
//+------------------------------------------------------------------+
//| ChartEvent function                                              |
//+------------------------------------------------------------------+
void OnChartEvent(const int id,
                  const long &lparam,
                  const double &dparam,
                  const string &sparam)
  {
  }
//+------------------------------------------------------------------+
//|    Writes one line to the current logfile as defined in init     |
//+------------------------------------------------------------------+
void writeToLogFile(string message)
  {//0
   string curTime=GetCurTime("Seconds");
   for(int i=0; i<5; i++)
     {//1
      int HFile=FileOpen(FileNameLog,FILE_READ|FILE_WRITE);
      if(HFile>0)
        {//2    
         FileSeek(HFile,0,SEEK_END);
         FileWrite(HFile,StringConcatenate(curTime,": ",message));
         FileFlush(HFile);
         FileClose(HFile);
         break;
           }else{Sleep(500); continue;
        }//2
     }//1
  }//0
//+-------------------------------------------------------------------------------------------+
//|    gets formatted actual time (just for logfile)   Detail = "Seconds", "Hours" or "Days"  |
//+-------------------------------------------------------------------------------------------+
string GetCurTime(string Detail)
  {//1 
   string StrMonth="",StrDay="",StrHour="",StrMinute="",StrSeconds="";
   RefreshRates();

   if(Detail=="Seconds")
     {
      if(Month()<10) { StrMonth="0"+Month(); } else { StrMonth=Month(); }
      if(Day()<10) { StrDay="0"+Day(); } else { StrDay=Day(); }
      if(Hour()<10) { StrHour="0"+Hour(); } else { StrHour=Hour(); }
      if(Minute()<10) { StrMinute="0"+Minute(); } else { StrMinute=Minute(); }
      if(Seconds()<10) { StrSeconds="0"+Seconds(); } else { StrSeconds=Seconds(); }
      return(""+StrDay+"."+StrMonth+"."+Year()+" "+StrHour+":"+StrMinute+":"+StrSeconds+" ");
     }
   if(Detail=="Hours")
     {
      if(Month()<10) { StrMonth="0"+Month(); } else { StrMonth=Month(); }
      if(Day()<10) { StrDay="0"+Day(); } else { StrDay=Day(); }
      if(Hour()<10) { StrHour="0"+Hour(); } else { StrHour=Hour(); }
      if(Minute()<10) { StrMinute="0"+Minute(); } else { StrMinute=Minute(); }
      if(Seconds()<10) { StrSeconds="0"+Seconds(); } else { StrSeconds=Seconds(); }
      return(""+StrDay+"."+StrMonth+"."+Year()+" "+StrHour+":00:"+"00 ");
     }
   if(Detail=="Days")
     {
      if(Month()<10) { StrMonth="0"+Month(); }else { StrMonth=Month(); }
      if(Day()<10) { StrDay="0"+Day(); } else { StrDay=Day(); }
      if(Hour()<10) { StrHour="0"+Hour(); } else { StrHour=Hour(); }
      if(Minute()<10) { StrMinute="0"+Minute(); } else { StrMinute=Minute(); }
      if(Seconds()<10) { StrSeconds="0"+Seconds(); } else { StrSeconds=Seconds(); }
      return(""+StrDay+"."+StrMonth+"."+Year()+" ");
     }
   return "NO TIME DETECTED";
  }//1 
//+------------------------------------------------------------------+
//---- codes returned from trade server                              |
//+------------------------------------------------------------------+
string ErrorDescription(int error_code)
  {
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
      case 4207: error_string="Graphical object error";                                   break;
      case 4210: error_string="Unknown chart property";                                   break;
      case 4211: error_string="Chart not found";                                          break;
      case 4212: error_string="Chart subwindow not found";                                break;
      case 4213: error_string="Chart indicator not found";                                break;
      case 4220: error_string="Symbol select error";                                      break;
      case 4250: error_string="otification error";                                        break;
      case 4251: error_string="Notification parameter error";                             break;
      case 4252: error_string="Notifications disabled";                                   break;
      case 4253: error_string="Notification send too frequent";                           break;
      case 5001: error_string="Too many opened files";                                    break;
      case 5002: error_string="Wrong file name";                                          break;
      case 5003: error_string="Too long file name";                                       break;
      case 5004: error_string="Cannot open file";                                         break;
      case 5005: error_string="Text file buffer allocation error";                        break;
      case 5006: error_string="Cannot delete file";                                       break;
      case 5007: error_string="Invalid file handle(file closed or was not opened)";       break;
      case 5008: error_string="Wrong file handle(handle index is out of handle table)";   break;
      case 5009: error_string="File must be opened with FILE_WRITE flag";                 break;
      case 5010: error_string="File must be opened with FILE_READ flag";                  break;
      case 5011: error_string="File must be opened with FILE_BIN flag";                   break;
      case 5012: error_string="File must be opened with FILE_TXT flag";                   break;
      case 5013: error_string="File must be opened with FILE_TXT or FILE_CSV flag";       break;
      case 5014: error_string="File must be opened with FILE_CSV flag";                   break;
      case 5015: error_string="File read error";                                          break;
      case 5016: error_string="File write error";                                         break;
      case 5017: error_string="String size must be specified for binary file";            break;
      case 5018: error_string="Incompatible file(for string arrays-TXT,for others-BIN)";  break;
      case 5019: error_string="File is directory not file";                               break;
      case 5020: error_string="File does not exist";                                      break;
      case 5021: error_string="File cannot be rewritten";                                 break;
      case 5022: error_string="Wrong directory name";                                     break;
      case 5023: error_string="Directory does not exist";                                 break;
      case 5024: error_string="Specified file is not directory";                          break;
      case 5025: error_string="Cannot delete directory";                                  break;
      case 5026: error_string="Cannot clean directory";                                   break;
      case 5027: error_string="Array resize error";                                       break;
      case 5028: error_string="String resize error";                                      break;
      case 5029: error_string="Structure contains strings or dynamic arrays";             break;
      default:   error_string="unknown error";
     }
//----
   return(error_string);
  }
//+------------------------------------------------------------------+
