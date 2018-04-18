package net.ngorham.todolist;

public class ViewSelectOption {
    //Private variables
    private String option;
    private int icon;

    public ViewSelectOption(String option, int icon){
        setOption(option);
        setIcon(icon);
    }

    public void setOption(String option){
        this.option = option;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

    public String getOption(){
        return option;
    }

    public int getIcon(){
        return icon;
    }
}
