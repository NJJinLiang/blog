import java.util.ArrayList;
import java.util.List;

public class Nfc implements Cloneable{

    public static void main(String[] strings){
        List<Bean> list = new ArrayList<>();
        list.add(new Bean(1 ,11));
        list.add(new Bean(2 ,11));
        list.add(new Bean(3 ,11));
        list.add(new Bean(4 ,11));

        for(Bean bean : list){
            if(bean.getId() == 2){
                list.remove(bean);
                break;
            }
        }
        for(Bean bean : list){
            System.out.println(bean.getId() + "--" + bean.getName());
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    static class Bean {
        private int id;
        private int name;

        public Bean(int id, int name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getName() {
            return name;
        }

        public void setName(int name) {
            this.name = name;
        }
    }
}
