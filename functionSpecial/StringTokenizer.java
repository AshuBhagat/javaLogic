 int billNumberId=1203;
 String IdString="1201$1202$1203";
 StringTokenizer st = new StringTokenizer(IdString,"$"); 
	int a=0;
	while(st.hasMoreTokens()) 
	{ 
	 String key = st.nextToken(); 
	 a=Integer.parseInt(key);
	if(a==billNumberId)
	{
	 bindbillNumberIdString=billNumberIdString;
	 //return false;
	}
	}
	if(a!=billNumberId)
	{
	 bindbillNumberIdString=billNumberIdString+billNumberId+"$";
	}
	                 