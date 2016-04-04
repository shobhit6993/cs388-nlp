from matplotlib import pyplot as plt
import glob


def plot_graph(folder, ax, label, color, xlabel, ylabel):
    files = glob.glob(folder)
    files.sort()

    precision = []
    recall = []
    f1 = []
    x = []

    for file in files:
      if "README" in file:
        continue

        x.append(int(file.split('/')[-1]))
        with open(file, 'r') as f:
            for line in f:
                if "summary evalb" in line and "pcfg" in line:
                    tokens = line.split(':')
                    precision.append(float(tokens[2].split(' ')[1]))
                    recall.append(float(tokens[3].split(' ')[1]))
                    f1.append(float(tokens[4].split(' ')[1].split('\n')[0]))

    ax.set_autoscaley_on(True)
    # plt.ylim([65.0, 86.0])
    plt.plot(x, f1, alpha=1.0,
             label=label, marker='o', color=color)
    plt.legend(loc="best", framealpha=0.3)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    # fig.savefig("../fig/sigma_%s" % j + ".png")


def expt_1():
    fig, ax = plt.subplots(1, 1)
    plot_graph("../logs/1a/*", ax,
               "wsj seed, brown self-train, brown test", "red",
               "Size of seed set", "F1 score")
    plot_graph("../logs/1b/*", ax,
               "wsj seed, no self-train, brown test", "green",
               "Size of seed set", "F1 score")
    plot_graph("../logs/1c/*", ax,
               "wsj seed, no self-train, wsj test", "blue",
               "Size of seed set", "F1 score")


def expt_2():
    fig, ax = plt.subplots(1, 1)
    plot_graph("../logs/2a/*", ax,
               "wsj seed, brown self-train, brown test", "red",
               "Size of self-training set", "F1 score")


def expt_3():
    fig, ax = plt.subplots(1, 1)
    plot_graph("../logs/3a/*", ax,
               "brown seed, wsj self-train, wsj test", "red",
               "Size of seed set", "F1 score")
    plot_graph("../logs/3b/*", ax,
               "brown seed, no self-train, wsj test", "green",
               "Size of seed set", "F1 score")
    plot_graph("../logs/3c/*", ax,
               "brown seed, no self-train, brown test", "blue",
               "Size of seed set", "F1 score")


def expt_4():
    fig, ax = plt.subplots(1, 1)
    plot_graph("../logs/4a/*", ax,
               "brown seed, wsj self-train, wsj test", "red",
               "Size of self-training set", "F1 score")

if __name__ == '__main__':
    expt_1()
    expt_2()
    expt_3()
    expt_4()
    plt.show()
