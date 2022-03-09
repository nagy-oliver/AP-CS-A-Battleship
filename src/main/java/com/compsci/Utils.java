package com.compsci;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static class IPv4ValidatorRegex {
        
        private static final String IPV4_PATTERN =
                "^(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.(?!$)|$)){4}$";
        
        private static final Pattern pattern = Pattern.compile(IPV4_PATTERN);
        
        public static boolean isValid(final String a) {
            Matcher matcher = pattern.matcher(a);
            return matcher.matches();
        }
    }
    public static final String testBundle = "d87ca44f7d438f9b1275d617d29d311b99c26ce6ee2e35addb7939b180e0290da5ff2a5a064ff5699b4bf751da70140aa4fb2c45d73af00b0bc653e495d45ef4dd75744beb5f68374eecd94a55cd509dbf3f5e7c9da246327ddaa39a0e9a6bbcfcc4fc3d6c43ee158ff0514c3b0157e55a6e73c84a9d507e579ef12f1ee94a82a3d833bd7c1e6e5cf8039b84efd249f2f89bfcd56f7052e05aff36abfd083c78828109bf7fb68cfdcf29b4158ebea5d42f2a78f9d28addeee1a527dbe9514f426c8f52c0cf198ed883468b9b6f08e96a873db39fcedf07f592783c343b94d4a593318d756edb172c85d1a6bef8b849badbd95db223bbd3c6e5e7e84565cabbed83966657703defa3c0ab444fc6a29edec4325ac11930444a6a6e0ae6ababe56aebf0a57dc1a723e072b0e6075a0f4688b5e52c0c7b7ca97c58a0cbec786aa9f2";
}
