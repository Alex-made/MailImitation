// implement UntrustworthyMailWorker, Spy, Inspector, Thief, StolenPackageException, IllegalPackageException as public static classes here
public static class UntrustworthyMailWorker implements MailService {
    private final MailService realMailService = new RealMailService();
    private MailService[] mailServices;
    public UntrustworthyMailWorker(MailService[] services){
        mailServices = services;
    }

    public MailService getRealMailService(){
        return realMailService;
    }

    @Override
    public Sendable processMail(Sendable mail) {
        Sendable processed = mail;
        for (int i = 0; i < mailServices.length; i++) {
            processed = mailServices[i].processMail(processed);
        }
        return realMailService.processMail(mail);
    }
}

//логирует все письма, что через него проходит getFrom(), getTo(), getMessage().
public static class Spy implements MailService {
    private Logger LOGGER;    
    
    public Spy(Logger L){
        LOGGER = L;
        LOGGER.setLevel(Level.INFO);
    }  
    
    
    
    @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage) {
                MailMessage ms = (MailMessage)mail;
                if (ms.getFrom().equals(AUSTIN_POWERS) || ms.getTo().equals(AUSTIN_POWERS)) {
                    LOGGER.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"",
                            new Object[] { ms.getFrom(), ms.getTo(), ms.getMessage() });
                } else {
                    LOGGER.log(Level.INFO, "Usual correspondence: from {0} to {1}",
                            new Object[] { ms.getFrom(), ms.getTo() });
                }
            }
            return mail;
        }
}

public static class Thief implements MailService {
    static int price; 
    int stolenValue = 0;
    
    
    public Thief (int price){
        this.price = price;
    }
    
    static int getPrice(){
        return price;
    }
    
    @Override
    public Sendable processMail(Sendable mail){
    
        if (mail.getClass() == MailPackage.class)
        {Package pack = ((MailPackage) mail).getContent();
            if (pack.getPrice() >= this.getPrice()){
                  mail = new MailPackage(mail.getFrom(), mail.getTo(), 
                                         (new Package ("stones instead of " + pack.getContent(), 0)) );
                 
                 stolenValue = stolenValue + pack.getPrice();
            }    
           
        }
    return mail;
    }
    
    public int getStolenValue(){
        return stolenValue;   
    }    
}    

//Исключения для класса Inspector
public static class IllegalPackageException extends RuntimeException {
        
}

public static class StolenPackageException extends RuntimeException{
    
}
//*************


public static class Inspector implements MailService {
    
    @Override
    public Sendable processMail(Sendable mail) {
        if (mail.getClass() == MailPackage.class)
        {   Package pack = ((MailPackage) mail).getContent();
            if(  pack.getContent().equals(WEAPONS) || pack.getContent().equals(BANNED_SUBSTANCE)  )
            {throw new IllegalPackageException();}
               
            if(pack.getContent().indexOf("stones") == 0)
            {throw new StolenPackageException();}
        }
        
    return mail;
    }    
    
}