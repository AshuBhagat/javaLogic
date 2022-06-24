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
	                 